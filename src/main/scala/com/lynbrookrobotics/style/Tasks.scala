package com.lynbrookrobotics.style

import org.eclipse.egit.github.core.{CommitComment, RepositoryId}
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.service.PullRequestService
import scala.collection.JavaConversions._
import sbt._

import scala.io.Source
import scala.xml.XML

object Tasks {
  private def parseHunkHeader(line: String): Int = {
    val innerHeader = line.dropWhile(_ == '@').takeWhile(_ != '@').trim
    innerHeader.split('+').last.split(',').head.toInt
  }

  private lazy val githubToken = sys.env("SCALASTYLE_BOT_TOKEN")
  private lazy val user = sys.env("SCALASTYLE_BOT_USER")
  private lazy val prNumber: Int = sys.env("TRAVIS_PULL_REQUEST").toInt
  private lazy val orgName: String = sys.env("TRAVIS_REPO_SLUG").split('/').head
  private lazy val repoName: String = sys.env("TRAVIS_REPO_SLUG").split('/').last

  lazy val publishStyleComments = Def.task {
    val github = new GitHubClient().setOAuth2Token(githubToken)
    val pr = new PullRequestService(github)
    val repo = new RepositoryId(orgName, repoName)
    val files = pr.getFiles(repo, prNumber).map(f => f.getFilename -> f).toMap
    val lastCommit = pr.getCommits(repo, prNumber).last

    pr.getComments(repo, prNumber).foreach { comment =>
      if (comment.getUser.getLogin == user) {
        pr.deleteComment(repo, comment.getId)
      }
    }

    val checkstyleXML = XML.loadFile(sbt.Keys.target.value / "scalastyle-result.xml")

    checkstyleXML.child.filter(_.label == "file").map { file =>
      val filePath = (file \ "@name").text
      (new File(filePath).relativeTo(sbt.Keys.baseDirectory.value).get.getPath, file)
    }.filter(f => files.contains(f._1)).foreach { case (path, file) =>
      println(path)

      Option(files(path).getPatch).foreach { p =>
        val patch = p.split('\n')
        // diff index, source index, map
        val diffIndexForSource = patch.
          foldLeft((0, -1, Map.empty[Int, Int])) { case ((diffIndex, sourceIndex, map), line) =>
            if (line.startsWith("@")) {
              val nextSourceIndex = parseHunkHeader(line)
              (diffIndex + 1, nextSourceIndex, map)
            } else {
              val nextDiffIndex = diffIndex + 1

              if (line.startsWith("-")) {
                // not part of the new source
                (nextDiffIndex, sourceIndex, map)
              } else {
                (nextDiffIndex, sourceIndex + 1, map + (sourceIndex -> diffIndex))
              }
            }
          }._3

        file.child.filter(_.label == "error").foreach { error =>
          val line = (error \ "@line").text.toInt

          if (line >= 0) {
            val message = (error \ "@message").text
            val severity = (error \ "@severity").text

            val commentMessage = s"Style $severity: $message" // Style warning/error: ...

            diffIndexForSource.get(line).foreach { idx =>
              val comment = new CommitComment().setPosition(idx).
                                                setPath(path).
                                                setCommitId(lastCommit.getSha)
              comment.setBody(commentMessage)
              pr.createComment(repo, prNumber, comment)
            }
          }
        }
      }
    }
  }
}
