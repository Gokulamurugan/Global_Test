package Simulations
import io.gatling.http.Predef._
import io.gatling.core.Predef._

import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps

class check_status_code extends Simulation {

  //http config
  val httpconf = http.baseUrl("http://localhost:8080/app/")
    .header(name = "Accept", "Application/json")
  //Scenario Definition
  val scn = scenario("VideoGame 3 API with Pause Time")
    .exec(http("Get all games")
      .get("videogames")
      .check(status.is(200)))
    .pause(duration = 10)

    .exec(http("Get specific game")
      .get("videogames/1")
    .check(status.in(200 to 210)))
    .pause(2, 20)

    .exec(http("Back to all videogames")
      .get("videogames")
    .check(status.not(400),status.not(500)))
    .pause(2000.milliseconds)
  //Load test scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpconf)

}
