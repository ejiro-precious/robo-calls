package com.africastalking.robo
package utils

import scala.xml.Elem

object XMLResponses {

  val home: String =
    """
      <h1>Welcome to Robo Call Back Url Api, this is health check</h1>
    """.stripMargin

  val rejectElem: Elem = <Response><Reject/></Response>

  val sayTryAgain =
    <Response>
      <Say>Your input is incorrect, try again</Say>
    </Response>

  /** stage 0  select your language**/
  val welcome =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-user-input.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/welcome_hausa.mp3" />
      </GetDigits>
      <Say>We did not get your input. Good bye</Say>
    </Response>

  /** stage 1  select what you want to hear prayer, food_tips etc**/
  val handle_eng =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-2-english.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/english_main_menu.mp3" />
      </GetDigits>
    </Response>

  val handle_hausa =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-2-hausa.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/hausa_main_menu.mp3" />
      </GetDigits>
    </Response>

  /** stage 2 select the type of prayer, names **/
  // English
  val eng_pray =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-3-english-prayer.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/adhkar_prayers/prayer_menu.mp3" />
      </GetDigits>
    </Response>

  val eng_names =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/99names.mp3" />
    </Response>

  val eng_foodtips =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-3-english-food.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/food_tips/food_tips_menu.mp3" />
      </GetDigits>
    </Response>

  val eng_health =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-3-english-health.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/tips_on_health/health_menu.mp3" />
      </GetDigits>
    </Response>

  val eng_main4rm2 =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-2-english.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/english_main_menu.mp3" />
      </GetDigits>
      <Say>We did not get your input. Good bye</Say>
    </Response>

  // Hausa
  val hausa_pray =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-3-hausa-prayer.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/adhkar_prayers/menu.mp3" />
      </GetDigits>
    </Response>

  val hausa_names =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/99names.mp3" />
    </Response>

  val hausa_foodtips =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-3-hausa-food.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/food_tips/menu.mp3" />
      </GetDigits>
    </Response>

  val hausa_health =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-3-hausa-health.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/menu.mp3" />
      </GetDigits>
    </Response>

  val hausa_main2 =
    <Response>
      <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-2-hausa.php" timeout="15">
        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/hausa_main_menu.mp3" />
      </GetDigits>
      <Say>We did not get your input. Good bye</Say>
    </Response>

  /** Stage 3 return or end here **/
  // English Prayer
  val prayer_eng_forgive =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/adhkar_prayers/prayer_on_forgiveness.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  val prayer_eng_victory =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/adhkar_prayers/prayer_on_victory.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  val prayer_eng_reward =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/adhkar_prayers/prayer_on_reward.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  val prayer_eng_knoweledge =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/adhkar_prayers/prayer_on_knowledge.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  // English Food
  val food_eng_suhoor =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/food_tips/suhoor_recipe.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  val food_eng_iftar =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/food_tips/iftar_recipe.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  // English health =
  val health_eng_important =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/tips_on_health/1_health_important.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  val health_eng_water =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/tips_on_health/2%20_water.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  val health_eng_time =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/tips_on_health/3_time_ramadan.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  val health_eng_sleep =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/tips_on_health/4_sleep.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/english/jingle-english.mp3" />
    </Response>

  // Hausa Prayer
  val prayer_hausa_forgive =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/adhkar_prayers/forgiveness_1.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

  val prayer_hausa_victory =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/adhkar_prayers/victory_2.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

  val prayer_hausa_reward =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/adhkar_prayers/reward_3.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

  val prayer_hausa_knoweledge =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/adhkar_prayers/know_4.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

  // Hausa Food
  val food_hausa_suhoor =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/food_tips/1_suhoor.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

  val food_hausa_iftar =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/food_tips/2_iftar.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

  // Hausa Health =
  val health_hausa_meals =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/1_meals.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

  val health_hausa_drink =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/2_drink.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

  val health_hausa_discipline =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/3_discipline.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

  val health_hausa_rest =
    <Response>
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/4_rest.mp3" />
      <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
    </Response>

}
