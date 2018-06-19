<?php
/**
 * Created by IntelliJ IDEA.
 * User: ejiro
 * Date: 6/12/18
 * Time: 12:38 AM
 */
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
require('db.php');
require('./vendor/autoload.php');
require_once "vendor/AT/AfricasTalkingGateway.php";
use GuzzleHttp\Client;
use Carbon\Carbon;
if (isset($_POST) && $_POST['direction'] == "Outbound") {
    if (isset($_POST['dtmfDigits']) && $_POST['isActive'] == "1") {
        $digit = $_POST['dtmfDigits'];
        $content;
        switch ($digit) {
            case '1':

                echo '
                <Response>
                    <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/1_meals.mp3" />
                    <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
                </Response>
                ';
                break;
            case '2':

                echo '
                <Response>
                    <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/2_drink.mp3" />
                    <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
                </Response>
                ';
                break;
            case '3':

                echo '
                <Response>
                    <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/3_discipline.mp3" />
                    <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
                </Response>
                ';
                break;
            case '4':

                echo '
                <Response>
                    <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/4_rest.mp3" />
                    <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/jingle-hausa.mp3" />
                </Response>
                ';
                break;
            case '0':
                $content = "Repeat menu options";
                echo '
                <Response>
                    <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-3-hausa-health.php" timeout="15">
                        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/menu.mp3" />
                    </GetDigits>    
                </Response>';
                break;
            case '#':
                $content = "Return to previous menu";
                echo '
                <Response>
                    <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-2-hausa.php" timeout="15">
                        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/hausa_main_menu.mp3" />
                    </GetDigits>
                    <Say>We did not get your input. Good bye</Say>
                </Response>';
                break;

            default:
                $content = "No input selected";
                echo '
                <Response>
                    <GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-stage-3-hausa-health.php" timeout="15">
                        <Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/menu.mp3" />
                    </GetDigits>    
                </Response>';
                break;
        }
        if ($digit == '1') {
            $content = "https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/1_meals.mp3";
        } else if ($digit == '2') {
            $content = "https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/2_drink.mp3";
        } else if ($digit == '3') {
            $content = "https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/3_discipline.mp3";
        } else if ($digit == '4') {
            $content = "https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/hausa/tips_on_health/4_rest.mp3";
        } else if ($digit == '#') {
            $content = "Return to previous menu";
        } else if ($digit == '0') {
            $content = "Repeat the options";
        } else {
            $content = "No option selected";
        }
        $sessionId = $_POST['sessionId'];
        $callerNumber = $_POST['callerNumber'];
        $query = mysqli_query($db, "UPDATE `outbound` SET digit_selected_3='$digit', content_digit_3='$content' WHERE sessionId='$sessionId' AND caller='$callerNumber'");
        // $dataQ = mysqli_query($data_x_db, "UPDATE `contacts` SET sent=1 WHERE phone_number='$callerNumber'");
    } else if (isset($_POST['status']) && isset($_POST['callSessionState']) && $_POST['isActive'] == "0") {

        $currencyCode = $_POST['currencyCode'];
        $destinationNumber = $_POST['destinationNumber'];
        $callStartTime = $_POST['callStartTime'];
        $direction = $_POST['direction'];
        $status = $_POST['status'];
        $amount = number_format($_POST['amount'], 2);
        $callerNumber = $_POST['callerNumber'];
        $DIS = $_POST['durationInSeconds'];
        $CSS = $_POST['callSessionState'];
        $sessionId = $_POST['sessionId'];
        $CCC = $_POST['callerCountryCode'];

        # QUERY USER
        $data_x_host = "107.170.7.207";
        $data_x_username = "chike";
        $data_x_pass = "Godalmighty1993";
        @$data_x_db = mysqli_connect($data_x_host, $data_x_username, $data_x_pass, 'data_x');
        $phone = preg_replace('/\+234/', '0', $callerNumber, 1);
        @$data_x_query = mysqli_query($data_x_db, "SELECT * FROM contacts WHERE phone_number='$phone'");

        if (mysqli_num_rows($data_x_query) > 0) {
            $user = mysqli_fetch_array($data_x_query);
            $lga = $user['lga'];
            $state = $user['state'];
            $gender = $user['gender'];
            $network = $user['network'];
            $datax_uid = $user['id'];

            $query = mysqli_query($db, "UPDATE `outbound` SET 
            status='$status', 
            callSessionState='$CSS', 
            callerCountryCode='$CCC', 
            currencyCode='$currencyCode', 
            amount='$amount', 
            durationInSecs='$DIS', 
            state='$state', 
            lga='$lga', 
            network='$network', 
            gender='$gender', 
            data_x_uuid='$datax_uid',
            call_time='$callStartTime', unique_caller='1' WHERE sessionId='$sessionId' AND caller='$callerNumber'");
        } else {
            $query = mysqli_query($db, "UPDATE `outbound` SET status='$status', callSessionState='$CSS', callerCountryCode='$CCC', currencyCode='$currencyCode', amount='$amount', durationInSecs='$DIS', call_time='$callStartTime' WHERE sessionId='$sessionId' AND caller='$callerNumber'");
        }
        if ($query) {
            if ($callerNumber) {
                $message = 'Sent and saved to the data-x database';
                $conQ = mysqli_query($db, "INSERT INTO logs (caller, message) VALUES ('$callerNumber', '$message')");
                if ($conQ) {
                    echo $callerNumber;die;
                } else {
                    echo $message;die;
                }
            }
        } else {
            echo mysqli_error($db);
        }
    }
}