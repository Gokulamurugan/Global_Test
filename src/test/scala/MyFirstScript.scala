import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MyF1irstScript extends Simulation{
  //http config
  val httpconfig = http.baseUrl(url="http://localhost:8080/app/")
    .header(name="Accept", value="application/json")
  // Scenario Defe
  val scn = scenario(scenarioName = "My First Test")
    .exec(http(requestName="Get All Games")
      .get("videogames"))
  //Load Scenario
  setUp(
    scn.inject(atOnceUsers(users = 2))
  ).protocols(httpconfig)
}
