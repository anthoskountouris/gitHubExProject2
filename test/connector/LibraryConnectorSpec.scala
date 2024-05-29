package connector

import baseSpec.BaseSpecWithApplication
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalTo, equalToJson, stubFor, urlEqualTo}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.contentAsJson

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
  }

}
