package service

import akka.http.scaladsl.model.Uri.Path
import cats.data.EitherT
import connector.LibraryConnector
import models.{APIError, DeleteFile, DirContent, FileContent, GitHubUser, NewFile, RepoContent, UpdatedFile, UserRepos}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.JsValue
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.ws.WSResponse
import play.mvc.BodyParser.Json

import javax.inject.Inject
import javax.swing.JDialog
import scala.concurrent.{ExecutionContext, Future}

class LibraryService @Inject()(connector:LibraryConnector){
  def getGithubUser(urlOverride: Option[String] = None, username:String)(implicit ex: ExecutionContext): EitherT[Future, APIError, GitHubUser] =
    connector.get[GitHubUser](urlOverride.getOrElse(s"https://api.github.com/users/$username"))

  def getUserRepos(urlOverride: Option[String] = None, username:String)(implicit ex: ExecutionContext): EitherT[Future, APIError, List[UserRepos]] =
    connector.get2[UserRepos](urlOverride.getOrElse(s"https://api.github.com/users/$username/repos"))

  def getRepoContent(urlOverride: Option[String] = None, username:String, repoName:String)(implicit ex: ExecutionContext): EitherT[Future, APIError, List[RepoContent]] =
    connector.get2[RepoContent](urlOverride.getOrElse(s"https://api.github.com/repos/$username/$repoName/contents/"))

  def getFileOrDirContent(urlOverride: Option[String] = None, username:String, repoName:String, path: String)(implicit ex: ExecutionContext): EitherT[Future, APIError, Either[List[DirContent], FileContent]] = {
//    getRepoContent(urlOverride, username, repoName).value.map{
//      case Right(content) =>  content.
      connector.get3[DirContent](urlOverride.getOrElse(s"https://api.github.com/repos/$username/$repoName/contents/$path")) orElse (
        connector.get3[FileContent](urlOverride.getOrElse(s"https://api.github.com/repos/$username/$repoName/contents/$path"))
      )
  }

  def createFile(urlOverride: Option[String] = None, username:String, repoName:String, path: String, dataModel:NewFile)(implicit ex: ExecutionContext): Future[Either[APIError,WSResponse]] ={
    val url = urlOverride.getOrElse(s"https://api.github.com/repos/$username/$repoName/contents/$path")
    println(s"Creating file at: $url")
    connector.post(url, dataModel = dataModel)
  }

  def updateFile(urlOverride: Option[String] = None, username:String, repoName:String, path: String, dataModel:UpdatedFile)(implicit ex: ExecutionContext): Future[Either[APIError,WSResponse]]  = {
    val url = urlOverride.getOrElse(s"https://api.github.com/repos/$username/$repoName/contents/$path")
    println(s"Updating file at: $url")
    connector.put(url, dataModel = dataModel)
  }
  def deleteFile(urlOverride: Option[String] = None, username:String, repoName:String, path: String, dataModel:DeleteFile)(implicit ex: ExecutionContext): Future[Either[APIError,WSResponse]]  = {
    val url = urlOverride.getOrElse(s"https://api.github.com/repos/$username/$repoName/contents/$path")
    println(s"Deleting file at: $url")
    connector.delete(url, payload = dataModel)
  }
}

// DIR
//
//[
//{
//  "name": "Search_Twitter_API.py",
//  "path": "Twitter_API/Search_Twitter_API.py",
//  "sha": "ae514a0a959fb8068be007115f9504adce32231a",
//  "size": 3253,
//  "url": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_API/Search_Twitter_API.py?ref=master",
//  "html_url": "https://github.com/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/blob/master/Twitter_API/Search_Twitter_API.py",
//  "git_url": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/git/blobs/ae514a0a959fb8068be007115f9504adce32231a",
//  "download_url": "https://raw.githubusercontent.com/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/master/Twitter_API/Search_Twitter_API.py",
//  "type": "file",
//  "_links": {
//    "self": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_API/Search_Twitter_API.py?ref=master",
//    "git": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/git/blobs/ae514a0a959fb8068be007115f9504adce32231a",
//    "html": "https://github.com/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/blob/master/Twitter_API/Search_Twitter_API.py"
//  }
//},
//{
//  "name": "Streaming_Twitter_te_Final.py",
//  "path": "Twitter_API/Streaming_Twitter_te_Final.py",
//  "sha": "35535c04e6edd79461f56a0088f81ce96ae83e36",
//  "size": 4489,
//  "url": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_API/Streaming_Twitter_te_Final.py?ref=master",
//  "html_url": "https://github.com/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/blob/master/Twitter_API/Streaming_Twitter_te_Final.py",
//  "git_url": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/git/blobs/35535c04e6edd79461f56a0088f81ce96ae83e36",
//  "download_url": "https://raw.githubusercontent.com/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/master/Twitter_API/Streaming_Twitter_te_Final.py",
//  "type": "file",
//  "_links": {
//    "self": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_API/Streaming_Twitter_te_Final.py?ref=master",
//    "git": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/git/blobs/35535c04e6edd79461f56a0088f81ce96ae83e36",
//    "html": "https://github.com/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/blob/master/Twitter_API/Streaming_Twitter_te_Final.py"
//  }
//}
//]


//FILE
//{
//  "name": "Search_Twitter_API.py",
//  "path": "Twitter_API/Search_Twitter_API.py",
//  "sha": "ae514a0a959fb8068be007115f9504adce32231a",
//  "size": 3253,
//  "url": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_API/Search_Twitter_API.py?ref=master",
//  "html_url": "https://github.com/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/blob/master/Twitter_API/Search_Twitter_API.py",
//  "git_url": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/git/blobs/ae514a0a959fb8068be007115f9504adce32231a",
//  "download_url": "https://raw.githubusercontent.com/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/master/Twitter_API/Search_Twitter_API.py",
//  "type": "file",
//  "content": "IyBodHRwczovL2RvY3MudHdlZXB5Lm9yZy9lbi92NC41LjAvY2xpZW50Lmh0\nbWwjdHdlZXB5LkNsaWVudC5zZWFyY2hfcmVjZW50X3R3ZWV0cwojIGh0dHBz\nOi8vZGV2ZWxvcGVyLnR3aXR0ZXIuY29tL2VuL2RvY3MvdHdpdHRlci1hcGkv\ndHdlZXRzL3NlYXJjaC9pbnRlZ3JhdGUvYnVpbGQtYS1xdWVyeSNsaXN0CiMg\naHR0cHM6Ly9kZXZlbG9wZXIudHdpdHRlci5jb20vZW4vZG9jcy90d2l0dGVy\nLWFwaS90d2VldHMvc2VhcmNoL2ludGVncmF0ZS9idWlsZC1hLXF1ZXJ5CiMg\naHR0cHM6Ly9kZXZlbG9wZXIudHdpdHRlci5jb20vZW4vZG9jcy90d2l0dGVy\nLWFwaS9kYXRhLWRpY3Rpb25hcnkvb2JqZWN0LW1vZGVsL3BsYWNlCiMgaHR0\ncHM6Ly9kb2NzLnR3ZWVweS5vcmcvZW4vdjQuNS4wL2NsaWVudC5odG1sI3Bs\nYWNlLWZpZWxkcy1wYXJhbWV0ZXIKIyBodHRwczovL2Rldi50by90d2l0dGVy\nZGV2L2EtY29tcHJlaGVuc2l2ZS1ndWlkZS1mb3ItdXNpbmctdGhlLXR3aXR0\nZXItYXBpLXYyLXVzaW5nLXR3ZWVweS1pbi1weXRob24tMTVkOQojIGh0dHBz\nOi8vd3d3LnlvdXR1YmUuY29tL3dhdGNoP3Y9clFFc0lzOUxFUk0KCgppbXBv\ncnQgdHdlZXB5CiNpbXBvcnQgdHdpdHRlciBrZXlzIGFuZCB0b2tlbnMKaW1w\nb3J0IGNvbmZpZwppbXBvcnQgcmFuZG9tCmltcG9ydCBqc29uCmltcG9ydCBv\ncGVucHl4bAoKIyBHZXR0aW5nIHRoZSBDbGllbnQgd2l0aCB0aGUgS2V5cyBh\nbmQgQWNjZXNzIFRva2VucwpkZWYgZ2V0Q2xpZW50KCk6CgljbGllbnQgPSB0\nd2VlcHkuQ2xpZW50KGJlYXJlcl90b2tlbj1jb25maWcuYmVhcmVyX3Rva2Vu\nLCAKCQkJCQkJICAgY29uc3VtZXJfa2V5PWNvbmZpZy5jb25zdW1lcl9rZXks\nIAoJCQkJCQkgICBjb25zdW1lcl9zZWNyZXQ9Y29uZmlnLmNvbnN1bWVyX3Nl\nY3JldCwgCgkJCQkJCSAgIGFjY2Vzc190b2tlbj1jb25maWcuYWNjZXNzX3Rv\na2VuLCAKCQkJCQkJICAgYWNjZXNzX3Rva2VuX3NlY3JldD1jb25maWcuYWNj\nZXNzX3Rva2VuX3NlY3JldCkKCXJldHVybiBjbGllbnQKCiNHZXR0aW5nIHRo\nZSB0d2VldHMKZGVmIHNlYXJjaFR3ZWV0cyhxdWVyeSk6CgkjU2V0dGluZyB1\ncCB0aGUgY2xpZW50IHRvIHRoZSB2YXJpYWJsZSBjbGllbnQKCWNsaWVudCA9\nIGdldENsaWVudCgpCgoJIyBUaGUgcmVjZW50IHNlYXJjaCBlbmRwb2ludCBy\nZXR1cm5zIFR3ZWV0cyBmcm9tIHRoZSBsYXN0IHNldmVuIGRheXMgdGhhdCBt\nYXRjaCBhIHNlYXJjaCBxdWVyeQoJIyBJbiB0aGlzIHByb2plY3QgSSB3aWxs\nIGJlIGdldHRpbmcgdHdlZXRzIGZyb20gMDQvMDIvMjAyMiB0byAuLi4KCSMg\nZnJvbSAxMDowMCB0byAyMDowMCBldmVyeSBkYXkgdG8gZ2V0IGFzIG11Y2gg\ndHdlZXRzIGFzIHBvc3NpYmxlLgoJIyBJIGhhdmUgZ290IHRoZSBFbGV2YXRl\nZCBBY2Nlc3Mgb2YgdGhlIFR3aXR0ZXIgQVBJIHNvIEkgYW0gYWxsb3dlZCB0\nbyBnZXQKCSMgbWF4aW11bSAxMDAgcmVzdWx0cyBwZXIgcmVxdWVzdC4gU28g\nZXZlcnkgaG91ciBJIHJldHJpZXZlIGFwcHJveGltYXRlbHkgMTAwIHVuaXF1\nZSB0d2VldHMKCSMgMTAtMTEKCSMgMTEtMTIKCSMgMTItMTMKCSMgMTMtMTQK\nCSMgMTQtMTUKCSMgMTUtMTYKCSMgMTYtMTcKCSMgMTctMTgKCSMgMTgtMTkK\nCSMgMTktMjAKCQoJdHdlZXRzID0gY2xpZW50LnNlYXJjaF9yZWNlbnRfdHdl\nZXRzKHF1ZXJ5PXF1ZXJ5LCAKCQkJCQkJCQkJCSB0d2VldF9maWVsZHM9Wyd0\nZXh0JywnY3JlYXRlZF9hdCcsJ2dlbyddLCAKCQkJCQkJCQkJCSBwbGFjZV9m\naWVsZHM9WydmdWxsX25hbWUnLCdnZW8nXSwKCQkJCQkJCQkJCSBleHBhbnNp\nb25zID0gJ2dlby5wbGFjZV9pZCcsCgkJCQkJCQkJCQkgc3RhcnRfdGltZT0n\nMjAyMi0wMy0wMlQxOTowMDowMC0wMDowMCcsCgkJCQkJCQkJCQkgZW5kX3Rp\nbWUgPSAnMjAyMi0wMy0wMlQyMDowMDowMC0wMDowMCcsCgkJCQkJCQkJCQkg\nbWF4X3Jlc3VsdHM9MTAwKQoKCQkgCgoJdHdlZXRfZGF0YSA9IHR3ZWV0cy5k\nYXRhCgl0d2VldF9pbmNsdWRlcyA9IHR3ZWV0cy5pbmNsdWRlcwoKCSNsaXN0\nIG9mIHBsYWNlcyBmcm9tIHRoZSBpbmNsdWRlcyBvYmplY3QuIE5vdCBldmVy\neSB0d2VldCBpcyBnZW8tdGFnZ2VkCglpZiB0d2VldF9pbmNsdWRlczoKCQlw\nbGFjZXMgPSB7cFsnaWQnXTogcCBmb3IgcCBpbiB0d2VldHMuaW5jbHVkZXNb\nJ3BsYWNlcyddfQoKCSNvcGVuIGV4Y2VsIGRvY3VtZW50Cgl3YiA9IG9wZW5w\neXhsLmxvYWRfd29ya2Jvb2soJ1R3aXR0ZXJfRGF0YXNldDIueGxzeCcpCgoJ\naWYgbm90IHR3ZWV0X2RhdGEgaXMgTm9uZSBhbmQgbGVuKHR3ZWV0X2RhdGEp\nID4gMDoKCgkJZm9yIHR3ZWV0IGluIHR3ZWV0X2RhdGE6CgkJCWV4Y2VsX2lu\ncHV0X2xpc3QgPSBbXQoKCQkJdHdlZXRfaWQgPSB0d2VldC5pZAoJCQl0d2Vl\ndF90ZXh0ID0gc3RyKHR3ZWV0LnRleHQpCgoJCQknJycgCgkJCQlJbiBvdXIg\ncmVzcG9uc2UsIHdlIGdldCB0aGUgbGlzdCBvZiBwbGFjZXMgZnJvbSB0aGUg\naW5jbHVkZXMgb2JqZWN0LAoJCQkJYW5kIHdlIG1hdGNoIG9uIHRoZSBwbGFj\nZV9pZCB0byBnZXQgdGhlIHJlbGV2YW50IGdlbyBpbmZvcm1hdGlvbiAKCQkJ\nCWFzc29jaWF0ZWQgd2l0aCB0aGUgVHdlZXQKCQkJJycnCgoJCQlpZiBub3Qg\ndHdlZXQuZ2VvIGlzIE5vbmU6CgkJCQlpZiBwbGFjZXNbdHdlZXQuZ2VvWydw\nbGFjZV9pZCddXToKCQkJCQlwbGFjZSA9IHBsYWNlc1t0d2VldC5nZW9bJ3Bs\nYWNlX2lkJ11dCgkJCQkJbG9jYXRpb24gPSBwbGFjZS5mdWxsX25hbWUKCgkJ\nCWRhdGVfY3JlYXRlZCA9ICh0d2VldC5jcmVhdGVkX2F0LmRhdGUoKSkKCgkJ\nCWV4Y2VsX2lucHV0X2xpc3QuYXBwZW5kKHR3ZWV0X2lkKQoJCQlleGNlbF9p\nbnB1dF9saXN0LmFwcGVuZCh0d2VldF90ZXh0KQoJCQlleGNlbF9pbnB1dF9s\naXN0LmFwcGVuZChkYXRlX2NyZWF0ZWQpCgoJCQlpZiBub3QgdHdlZXQuZ2Vv\nIGlzIE5vbmU6CgkJCQlleGNlbF9pbnB1dF9saXN0LmFwcGVuZChsb2NhdGlv\nbikKCgkJCXRyeToKCQkJCXdzID0gd2JbJ1NoZWV0MSddCgkJCQl3cy5hcHBl\nbmQoZXhjZWxfaW5wdXRfbGlzdCkKCQkJCXdiLnNhdmUoJ1R3aXR0ZXJfRGF0\nYXNldDIueGxzeCcpCgkJCWV4Y2VwdDoKCQkJCXByaW50KCJBcHBlbmRpbmcg\nZmlsZSBlcnJvciIpCgoJZWxzZToKCQlyZXR1cm4gW10KCnR3ZWV0cyA9IHNl\nYXJjaFR3ZWV0cygnKCJlbGVjdHJpYyB2ZWhpY2xlcyIgT1IgI2VsZWN0cmlj\ndmVoaWNsZXMgT1IgI0VWcyBPUiAjRVYpIGxhbmc6ZW4gLWlzOnJldHdlZXQg\nLWlzOnJlcGx5JykKCg==\n",
//  "encoding": "base64",
//  "_links": {
//    "self": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_API/Search_Twitter_API.py?ref=master",
//    "git": "https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/git/blobs/ae514a0a959fb8068be007115f9504adce32231a",
//    "html": "https://github.com/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/blob/master/Twitter_API/Search_Twitter_API.py"
//  }
//}



