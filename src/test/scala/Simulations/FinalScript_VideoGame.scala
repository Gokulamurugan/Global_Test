package Simulations
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.core.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Random
import scala.concurrent.duration.DurationInt
import scala.util.Random.javaRandomToRandom

class FinalScript_VideoGame extends Simulation{

  private def getProperty(propertyName: String, defaultValue: String) = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  def userCount: Int = getProperty("USERS", "5").toInt
  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt
  def testDuration: Int = getProperty("DURATION", "60").toInt

  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration: ${testDuration} seconds")
  }
  //http Config
  def httpconf = http.baseUrl("http://localhost:8080/app/")
    .header("Accept","Application/json")
  var idNumbers = (11 to 200).iterator
  val rnd = new Random()
  val now = LocalDate.now()
  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  //Scenario design
  def getAllVideoGames(): ChainBuilder = {
    exec(http("Get all video games")
        .get("videogames")
        .check(status.is(200)))
  }.pause("2")

  val csvFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game-" + randomString(5)),
    "releaseDate" -> getRandomDate(now, rnd),
    "reviewScore" -> rnd.nextInt(100),
    "category" -> ("Category-" + randomString(6)),
    "rating" -> ("Rating-" + randomString(4))
  ))
  def createNewGame(): ChainBuilder =
    {
      feed(csvFeeder)
        .exec(http("Post New Game")
        .post("videogames")
        .body(ElFileBody("RequestBody/newgame.json")).asJson
        .check(status.is(200)))
  }.pause("2")

  def getSpecificVideoGame() = {
      exec(http("Get specific game")
        .get("videogames/${gameId}")
        .check(jsonPath("$.name").is("${name}"))
        .check(status.is(200)))
  }.pause("2")

  def deleteVideogame(): ChainBuilder =
    {
    exec(http("Delete Video Game")
    .delete("videogames/${gameId}")
    .check(status.is("200")))
  }.pause("2")

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }
  def getRandomDate(startDate: LocalDate, random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }
//Scenario design
  val scn = scenario("Final Test with Get,POST & Delete API's in Jenkins")
    .exec(getAllVideoGames())
    .exec(createNewGame())
    .exec(getSpecificVideoGame())
    .exec(deleteVideogame())
 //Load Simulation
  setUp(
    scn.inject(
      nothingFor(5.seconds),
      atOnceUsers(5),
      rampUsers(10) during (10.seconds)
    ).protocols(httpconf.inferHtmlResources()),
  )
}
