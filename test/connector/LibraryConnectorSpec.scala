package connector

import baseSpec.BaseSpecWithApplication
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, stubFor, urlEqualTo}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.{JsValue, Json}

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/*
WireMock test is designed to verify that the application correctly handles a specific API call,
ensuring that it processes the response correctly and behaves as expected.
 */

class LibraryConnectorSpec extends BaseSpecWithApplication {
  val Port = 9000
  val Host = "localhost"
  val wireMockServer = new WireMockServer(wireMockConfig().port(Port))

  override def beforeEach: Unit = {
    wireMockServer.start()
    WireMock.configureFor(Host, Port)
  }

  override def afterEach: Unit = {
    wireMockServer.stop()
  }

  val user1: JsValue = Json.obj(
    "login" -> "anthoskountouris",
    "created_at" -> "2015-07-18T12:45:58Z",
    "followers" -> 5,
    "following" -> 11
  )

  val user2: JsValue = Json.obj(
    "login" -> "shannadja",
    "created_at" -> "2021-01-10T08:16:05Z",
    "followers" -> 2,
    "following" -> 1
  )

  "WireMock" should {
    "/github/users/:username - stub GET request" in {
      val path = "/github/users/anthoskountouris"
      stubFor(WireMock.get(urlEqualTo(path))
        .willReturn(
          aResponse()
            .withStatus(200)
//            .withHeader("Content-Type", "text/plain")
//            .withBody("<div class=\"info\">\n        <h2>anthoskountouris</h2>\n        <h3>User Information</h3>\n        <p>Followers: 5</p>\n        <p>Following: 11</p>\n        <p>Created at: 2015-07-18T12:45:58Z</p>\n    </div>")
              .withHeader("Content-Type", "application/json")
              .withBody("{\"login\":\"anthoskountouris\",\"created_at\":\"2015-07-18T12:45:58Z\",\"followers\":5,\"following\":11}")
      ))

      val request = ws.url(s"http://$Host:$Port$path")
      val responseFuture = request.get()
      val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
      response.status mustEqual 200
//      response.body should include("<div class=\"info\">\n        <h2>anthoskountouris</h2>\n        <h3>User Information</h3>\n        <p>Followers: 5</p>\n        <p>Following: 11</p>\n        <p>Created at: 2015-07-18T12:45:58Z</p>\n    </div>")
      response.body shouldBe user1.toString()
    }

    "/github/addUser/:username - stub GET request" in {
      val path = "/github/addUser/shannadja"
      stubFor(WireMock.get((urlEqualTo(path)))
        .willReturn(
          aResponse()
            .withStatus(201)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"login\":\"shannadja\",\"created_at\":\"2021-01-10T08:16:05Z\",\"followers\":2,\"following\":1}")
        ))

      val request = ws.url(s"http://$Host:$Port$path")
      val responseFuture = request.get()
      val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
      response.status mustEqual 201
      response.body shouldBe user2.toString()
    }

    "/github/users/:username/repositories  - stub GET request" in {
      val path = "/github/users/anthoskountouris/repositories"
      stubFor(WireMock.get((urlEqualTo(path)))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("[{\"id\":507229316,\"name\":\"Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":683026434,\"name\":\"Analysis_of_Wikidata_editors_network\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":402405013,\"name\":\"anthoskountouris\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":397661974,\"name\":\"aviator-flight-center\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":504073080,\"name\":\"aviator_flight_center_manuals\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":783605338,\"name\":\"billGenerator\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257441059,\"name\":\"CM1101-Game-Project\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257428670,\"name\":\"CM1102-Easter-Date\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257430814,\"name\":\"CM1103-Squash-scoring-methods-comparison\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257446138,\"name\":\"CM1208-Document-Searching\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257443648,\"name\":\"CM1210-Magic-Square\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":331106570,\"name\":\"CM2104-Buffon-s-needle\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":375292834,\"name\":\"CM2203-recommendation-system\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":642052761,\"name\":\"Covid_19_Data_Analysis\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":711577062,\"name\":\"Disproportionality_in_stop_and_search_action_in_London\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":643846382,\"name\":\"Django_Template_App\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":776541064,\"name\":\"Expenses_Tracker\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":609288445,\"name\":\"Formula1_data_visualisation\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":349474482,\"name\":\"Full-Stack-Web-Development-with-Flask-Linkedin-Course\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":799899687,\"name\":\"gitHubExProject2\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":505946563,\"name\":\"Google_IT_Automation_Course\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":402406414,\"name\":\"gpsevdiotis\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":778913366,\"name\":\"guessWhoScala\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":375286152,\"name\":\"Lancometer3\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":683637510,\"name\":\"Leeds_Road_Network_Analysis\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":397950043,\"name\":\"mass-and-balance\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":642060632,\"name\":\"NLP_ACM_Turing_Awards_Project\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":404069297,\"name\":\"NodeJS_ShoppingCart\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":654956842,\"name\":\"Online_Bookstore\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":607657298,\"name\":\"OOP_eCommerce_system\",\"owner\":{\"login\":\"anthoskountouris\"}}]")
        ))

      val request = ws.url(s"http://$Host:$Port$path")
      val responseFuture = request.get()
      val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
      response.status mustEqual 200
      response.body shouldBe "[{\"id\":507229316,\"name\":\"Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":683026434,\"name\":\"Analysis_of_Wikidata_editors_network\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":402405013,\"name\":\"anthoskountouris\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":397661974,\"name\":\"aviator-flight-center\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":504073080,\"name\":\"aviator_flight_center_manuals\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":783605338,\"name\":\"billGenerator\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257441059,\"name\":\"CM1101-Game-Project\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257428670,\"name\":\"CM1102-Easter-Date\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257430814,\"name\":\"CM1103-Squash-scoring-methods-comparison\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257446138,\"name\":\"CM1208-Document-Searching\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":257443648,\"name\":\"CM1210-Magic-Square\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":331106570,\"name\":\"CM2104-Buffon-s-needle\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":375292834,\"name\":\"CM2203-recommendation-system\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":642052761,\"name\":\"Covid_19_Data_Analysis\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":711577062,\"name\":\"Disproportionality_in_stop_and_search_action_in_London\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":643846382,\"name\":\"Django_Template_App\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":776541064,\"name\":\"Expenses_Tracker\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":609288445,\"name\":\"Formula1_data_visualisation\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":349474482,\"name\":\"Full-Stack-Web-Development-with-Flask-Linkedin-Course\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":799899687,\"name\":\"gitHubExProject2\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":505946563,\"name\":\"Google_IT_Automation_Course\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":402406414,\"name\":\"gpsevdiotis\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":778913366,\"name\":\"guessWhoScala\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":375286152,\"name\":\"Lancometer3\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":683637510,\"name\":\"Leeds_Road_Network_Analysis\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":397950043,\"name\":\"mass-and-balance\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":642060632,\"name\":\"NLP_ACM_Turing_Awards_Project\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":404069297,\"name\":\"NodeJS_ShoppingCart\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":654956842,\"name\":\"Online_Bookstore\",\"owner\":{\"login\":\"anthoskountouris\"}},{\"id\":607657298,\"name\":\"OOP_eCommerce_system\",\"owner\":{\"login\":\"anthoskountouris\"}}]"
    }

    "/github/users/:username/repos/:repoName - stub GET request" in {
      val path = "/github/users/anthoskountouris/repos/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media"
            stubFor(WireMock.get((urlEqualTo(path)))
              .willReturn(
                aResponse()
                  .withStatus(200)
                  .withHeader("Content-Type", "application/json")
                  .withBody("[{\"name\":\".gitignore\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/.gitignore?ref=master\"},{\"name\":\"Analysis.ipynb\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Analysis.ipynb?ref=master\"},{\"name\":\"README.md\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/README.md?ref=master\"},{\"name\":\"Twitter_API\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_API?ref=master\"},{\"name\":\"Twitter_Dataset2.xlsx\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_Dataset2.xlsx?ref=master\"},{\"name\":\"twitter_mask.png\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/twitter_mask.png?ref=master\"}]")
              ))

            val request = ws.url(s"http://$Host:$Port$path")
            val responseFuture = request.get()
            val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
            response.status mustEqual 200
            response.body shouldBe "[{\"name\":\".gitignore\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/.gitignore?ref=master\"},{\"name\":\"Analysis.ipynb\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Analysis.ipynb?ref=master\"},{\"name\":\"README.md\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/README.md?ref=master\"},{\"name\":\"Twitter_API\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_API?ref=master\"},{\"name\":\"Twitter_Dataset2.xlsx\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/Twitter_Dataset2.xlsx?ref=master\"},{\"name\":\"twitter_mask.png\",\"url\":\"https://api.github.com/repos/anthoskountouris/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/contents/twitter_mask.png?ref=master\"}]"
    }

    "/github/users/:username/repos/:repoName/*path (1) - stub GET request" in {
      // file
      val path = "/github/users/anthoskountouris/repos/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/.gitignore"
      stubFor(WireMock.get((urlEqualTo(path)))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"name\":\".gitignore\",\"path\":\".gitignore\",\"type\":\"file\",\"content\":\"Ly5EU19TdG9yZQ==\\n\"}")
        )
      )

      val request = ws.url(s"http://$Host:$Port$path")
      val responseFuture = request.get()
      val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
      response.status mustEqual 200
      response.body shouldBe "{\"name\":\".gitignore\",\"path\":\".gitignore\",\"type\":\"file\",\"content\":\"Ly5EU19TdG9yZQ==\\n\"}"

    }

    "/github/users/:username/repos/:repoName/*path (2) - stub GET request" in {
      // directory
      val path = "/github/users/anthoskountouris/repos/Analysing_The_Discourse_Related_To_Electrical_Vehicles_On_Social_Media/Twitter_API"
      stubFor(WireMock.get((urlEqualTo(path)))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("[{\"name\":\"Search_Twitter_API.py\",\"path\":\"Twitter_API/Search_Twitter_API.py\",\"type\":\"file\"},{\"name\":\"Streaming_Twitter_te_Final.py\",\"path\":\"Twitter_API/Streaming_Twitter_te_Final.py\",\"type\":\"file\"}]")
        )
      )

      val request = ws.url(s"http://$Host:$Port$path")
      val responseFuture = request.get()
      val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
      response.status mustEqual 200
      response.body shouldBe "[{\"name\":\"Search_Twitter_API.py\",\"path\":\"Twitter_API/Search_Twitter_API.py\",\"type\":\"file\"},{\"name\":\"Streaming_Twitter_te_Final.py\",\"path\":\"Twitter_API/Streaming_Twitter_te_Final.py\",\"type\":\"file\"}]"
    }

    val file1: JsValue = Json.obj(
      "message" -> "my new new commit message",
      "content" -> "Apoel Thrilos"
    )

    "/github/users/:username/repos/:repoName/create/*path - stub PUT request" in {
      val path = "/github/users/anthoskountouris/repos/GitHubTest/create/ram5.py"
      stubFor(WireMock.put((urlEqualTo(path)))
        .willReturn(
          aResponse()
            .withStatus(201)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"content\":{\"name\":\"ram6.py\",\"path\":\"ram6.py\",\"sha\":\"1fcf2214bee9f427377ed5e1c228ab2168ae009a\",\"size\":13,\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/contents/ram6.py?ref=main\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/blob/main/ram6.py\",\"git_url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/blobs/1fcf2214bee9f427377ed5e1c228ab2168ae009a\",\"download_url\":\"https://raw.githubusercontent.com/anthoskountouris/GitHubTest/main/ram6.py\",\"type\":\"file\",\"_links\":{\"self\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/contents/ram6.py?ref=main\",\"git\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/blobs/1fcf2214bee9f427377ed5e1c228ab2168ae009a\",\"html\":\"https://github.com/anthoskountouris/GitHubTest/blob/main/ram6.py\"}},\"commit\":{\"sha\":\"5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"node_id\":\"C_kwDOMAAHFNoAKDVlZTcxZGU2ZTk0Mzc5NzY3NzJhODc0YTllOWUzNTdjYzcxNDljNWU\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"author\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T08:58:43Z\"},\"committer\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T08:58:43Z\"},\"tree\":{\"sha\":\"8c46dc3fe3c440994ab330ca6c30fe1f2582ff42\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/trees/8c46dc3fe3c440994ab330ca6c30fe1f2582ff42\"},\"message\":\"my new new commit message\",\"parents\":[{\"sha\":\"568039bee5133636d847e0741855d174d2643be1\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/568039bee5133636d847e0741855d174d2643be1\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/568039bee5133636d847e0741855d174d2643be1\"}],\"verification\":{\"verified\":false,\"reason\":\"unsigned\",\"signature\":null,\"payload\":null}}}")
        )
      )
      val request = ws.url(s"http://$Host:$Port$path")
      val responseFuture = request.put(file1)
      val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
      response.status mustEqual 201
      response.body shouldBe "{\"content\":{\"name\":\"ram6.py\",\"path\":\"ram6.py\",\"sha\":\"1fcf2214bee9f427377ed5e1c228ab2168ae009a\",\"size\":13,\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/contents/ram6.py?ref=main\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/blob/main/ram6.py\",\"git_url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/blobs/1fcf2214bee9f427377ed5e1c228ab2168ae009a\",\"download_url\":\"https://raw.githubusercontent.com/anthoskountouris/GitHubTest/main/ram6.py\",\"type\":\"file\",\"_links\":{\"self\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/contents/ram6.py?ref=main\",\"git\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/blobs/1fcf2214bee9f427377ed5e1c228ab2168ae009a\",\"html\":\"https://github.com/anthoskountouris/GitHubTest/blob/main/ram6.py\"}},\"commit\":{\"sha\":\"5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"node_id\":\"C_kwDOMAAHFNoAKDVlZTcxZGU2ZTk0Mzc5NzY3NzJhODc0YTllOWUzNTdjYzcxNDljNWU\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"author\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T08:58:43Z\"},\"committer\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T08:58:43Z\"},\"tree\":{\"sha\":\"8c46dc3fe3c440994ab330ca6c30fe1f2582ff42\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/trees/8c46dc3fe3c440994ab330ca6c30fe1f2582ff42\"},\"message\":\"my new new commit message\",\"parents\":[{\"sha\":\"568039bee5133636d847e0741855d174d2643be1\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/568039bee5133636d847e0741855d174d2643be1\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/568039bee5133636d847e0741855d174d2643be1\"}],\"verification\":{\"verified\":false,\"reason\":\"unsigned\",\"signature\":null,\"payload\":null}}}"
    }

    val file2: JsValue = Json.obj(
      "message" -> "my new new commit message",
      "content" -> "Apoel Thrilos AU79 Orange Madness",
      "sha" -> "988713d6e6e3461dda90d1855bf1d6d8c86184db"
    )

    "/github/users/:username/repos/:repoName/update/*path - stub PUT request" in {
      val path = "/github/users/anthoskountouris/repos/GitHubTest/update/ram5.py"
      stubFor(WireMock.put((urlEqualTo(path)))
        .willReturn(
          aResponse()
            .withStatus(201)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"content\":{\"name\":\"ram5.py\",\"path\":\"ram5.py\",\"sha\":\"59539bb07188d94a802d973b51dedb366b40198d\",\"size\":33,\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/contents/ram5.py?ref=main\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/blob/main/ram5.py\",\"git_url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/blobs/59539bb07188d94a802d973b51dedb366b40198d\",\"download_url\":\"https://raw.githubusercontent.com/anthoskountouris/GitHubTest/main/ram5.py\",\"type\":\"file\",\"_links\":{\"self\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/contents/ram5.py?ref=main\",\"git\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/blobs/59539bb07188d94a802d973b51dedb366b40198d\",\"html\":\"https://github.com/anthoskountouris/GitHubTest/blob/main/ram5.py\"}},\"commit\":{\"sha\":\"fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"node_id\":\"C_kwDOMAAHFNoAKGZjZDBiNTAwZmVjOGVjYWMwNzVhNzBmNWJkOGMzZmIxNTAzOGJjNmE\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"author\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T09:50:12Z\"},\"committer\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T09:50:12Z\"},\"tree\":{\"sha\":\"8ae4463c937839edd70e2da7dedb373e9ed43026\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/trees/8ae4463c937839edd70e2da7dedb373e9ed43026\"},\"message\":\"my new commit message\",\"parents\":[{\"sha\":\"5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/5ee71de6e9437976772a874a9e9e357cc7149c5e\"}],\"verification\":{\"verified\":false,\"reason\":\"unsigned\",\"signature\":null,\"payload\":null}}}")
        )
      )
      val request = ws.url(s"http://$Host:$Port$path")
      val responseFuture = request.put(file2)
      val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
      response.status mustEqual 201
      response.body shouldBe "{\"content\":{\"name\":\"ram5.py\",\"path\":\"ram5.py\",\"sha\":\"59539bb07188d94a802d973b51dedb366b40198d\",\"size\":33,\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/contents/ram5.py?ref=main\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/blob/main/ram5.py\",\"git_url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/blobs/59539bb07188d94a802d973b51dedb366b40198d\",\"download_url\":\"https://raw.githubusercontent.com/anthoskountouris/GitHubTest/main/ram5.py\",\"type\":\"file\",\"_links\":{\"self\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/contents/ram5.py?ref=main\",\"git\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/blobs/59539bb07188d94a802d973b51dedb366b40198d\",\"html\":\"https://github.com/anthoskountouris/GitHubTest/blob/main/ram5.py\"}},\"commit\":{\"sha\":\"fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"node_id\":\"C_kwDOMAAHFNoAKGZjZDBiNTAwZmVjOGVjYWMwNzVhNzBmNWJkOGMzZmIxNTAzOGJjNmE\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"author\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T09:50:12Z\"},\"committer\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T09:50:12Z\"},\"tree\":{\"sha\":\"8ae4463c937839edd70e2da7dedb373e9ed43026\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/trees/8ae4463c937839edd70e2da7dedb373e9ed43026\"},\"message\":\"my new commit message\",\"parents\":[{\"sha\":\"5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/5ee71de6e9437976772a874a9e9e357cc7149c5e\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/5ee71de6e9437976772a874a9e9e357cc7149c5e\"}],\"verification\":{\"verified\":false,\"reason\":\"unsigned\",\"signature\":null,\"payload\":null}}}"
    }

    val file3: JsValue = Json.obj(
      "message" -> "my new new commit message",
      "sha" -> "59539bb07188d94a802d973b51dedb366b40198d"
    )

    "/github/users/:username/repos/:repoName/delete/*path - stub DELETE request" in {
      val path = "/github/users/anthoskountouris/repos/GitHubTest/delete/ram5.py"
      stubFor(WireMock.delete((urlEqualTo(path)))
        .willReturn(
          aResponse()
            .withStatus(202)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"content\":null,\"commit\":{\"sha\":\"e588fd32b769b314d6705fe65cfc840b8f20e4d2\",\"node_id\":\"C_kwDOMAAHFNoAKGU1ODhmZDMyYjc2OWIzMTRkNjcwNWZlNjVjZmM4NDBiOGYyMGU0ZDI\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/e588fd32b769b314d6705fe65cfc840b8f20e4d2\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/e588fd32b769b314d6705fe65cfc840b8f20e4d2\",\"author\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T10:04:58Z\"},\"committer\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T10:04:58Z\"},\"tree\":{\"sha\":\"231ad93783c1dbc93405eb56e64e9f1a930bd962\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/trees/231ad93783c1dbc93405eb56e64e9f1a930bd962\"},\"message\":\"my commit new message\",\"parents\":[{\"sha\":\"fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\"}],\"verification\":{\"verified\":false,\"reason\":\"unsigned\",\"signature\":null,\"payload\":null}}}")
        )
      )

      val request = ws.url(s"http://$Host:$Port$path")
      val responseFuture = request.delete()
      val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
      response.status mustEqual 202
      response.body shouldBe "{\"content\":null,\"commit\":{\"sha\":\"e588fd32b769b314d6705fe65cfc840b8f20e4d2\",\"node_id\":\"C_kwDOMAAHFNoAKGU1ODhmZDMyYjc2OWIzMTRkNjcwNWZlNjVjZmM4NDBiOGYyMGU0ZDI\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/e588fd32b769b314d6705fe65cfc840b8f20e4d2\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/e588fd32b769b314d6705fe65cfc840b8f20e4d2\",\"author\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T10:04:58Z\"},\"committer\":{\"name\":\"Anthos Kountouris\",\"email\":\"anthos.kountouris@gmail.com\",\"date\":\"2024-05-30T10:04:58Z\"},\"tree\":{\"sha\":\"231ad93783c1dbc93405eb56e64e9f1a930bd962\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/trees/231ad93783c1dbc93405eb56e64e9f1a930bd962\"},\"message\":\"my commit new message\",\"parents\":[{\"sha\":\"fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"url\":\"https://api.github.com/repos/anthoskountouris/GitHubTest/git/commits/fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\",\"html_url\":\"https://github.com/anthoskountouris/GitHubTest/commit/fcd0b500fec8ecac075a70f5bd8c3fb15038bc6a\"}],\"verification\":{\"verified\":false,\"reason\":\"unsigned\",\"signature\":null,\"payload\":null}}}"
    }
  }

}
