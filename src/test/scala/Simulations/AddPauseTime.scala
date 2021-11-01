package Simulations
import io.gatling.http.Predef._
import io.gatling.core.Predef._

import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps

class AddPauseTime extends Simulation {

  //http config
  val httpconf = http.baseUrl("http://localhost:8080/app/")
    .header(name = "Accept", "Application/json")
  //Scenario Definition
  val scn = scenario("VideoGame 3 API with Pause Time")
    .exec(http("Get all games")
      .get("videogames"))
    .pause(duration = 27)

    .exec(http("Get specific game")
      .get("videogames/1"))
    .pause(2, 20)

    .exec(http("Back to all videogames")
      .get("videogames"))
    .pause(2000.milliseconds)
  //Load test scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpconf)

}
