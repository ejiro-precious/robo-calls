
<?php
/**
 * Created by IntelliJ IDEA.
 * User: ejiro
 * Date: 6/11/18
 * Time: 11:04 PM
 */

    require('db.php');
	require('./vendor/autoload.php');
	use GuzzleHttp\Client;
	echo 'yes'; die;

	if (isset($_POST) && isset($_POST['direction'])) {
        #CHECK FOR INCOMING CALLS
        if ($_POST['direction'] == "Inbound") {
            if ($_POST['callSessionState'] !== "Completed") {
                $res = "<Response>
							<Reject />
						</Response>";
                echo $res;
            }else if ($_POST['callSessionState'] == "Completed" && $_POST['status'] == "Success") {
                #STORE INBOUND CALLS
                $callSession = $_POST['callSessionState'];
                $countryCode = $_POST['callerCountryCode'];
                $duration = $_POST['durationInSeconds'];
                $status = $_POST['status'];
                $sessionId = $_POST['sessionId'];
                $currency = $_POST['currencyCode'];
                $callTime = $_POST['callStartTime'];
                $amount = $_POST['amount'];
                $caller = $_POST['callerNumber'];
                $destination = $_POST['destinationNumber'];

                $check =  mysqli_query($db, " SELECT * FROM `inbound` WHERE caller='$caller'");
                if (mysqli_num_rows($check) > 0) {
                    $client = new Client([
                        'base_uri' => 'https://voice.africastalking.com',
                        'curl' => [
                            CURLOPT_SSL_VERIFYPEER => false
                        ]
                    ]);
                    $query = [
                        "from" => $destination,
                        "to" => $caller,
                        "username" => "audioads"
                    ];
                    $body = http_build_query($query, '', '&');
                    $response = $client->request('POST', '/call', [
                        'form_params' => $query,
                        'headers' => $headers
                    ]);
                } else {
                    $query = mysqli_query($db, "INSERT INTO `inbound` 
								( caller, callee, country_code, status, currency_code, amount, sessionId, sessionState, duration, callTime )
								VALUES ('$caller', '$destination', '$countryCode', '$status', '$currency', '$amount', '$sessionId', '$callSession', '$duration', '$callTime')");
                }
            }
        }

        else if ($_POST['direction'] == "Outbound") {
            if ($_POST['isActive'] == "1") {
                #CHECK FOR OUTBOUND CALLS THAT ARE ACTIVE
                $response = '
					<Response>
						<GetDigits numDigits="1" callbackUrl="http://35.224.233.192/handle-user-input.php" timeout="15">
							<Play url="https://s3.eu-west-2.amazonaws.com/peakramadan/peak_audio/welcome_hausa.mp3" />
						</GetDigits>
						<Say>We did not get your input. Good bye</Say>
					</Response>
				';
                echo $response;
            } else if ($_POST['isActive'] == "0") {
                if ($_POST['destinationNumber']) {
                    $currencyCode = $_POST['currencyCode'];
                    $destinationNumber = $_POST['destinationNumber'];
                    $callerNumber = $_POST['callerNumber'];
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
                    $phone = str_replace("+234", "0", $callerNumber);
                    $data_x_query = mysqli_query($data_x_db, "SELECT * FROM contacts WHERE phone_number='$phone'");


                    if (mysqli_num_rows($data_x_query) > 0) {
                        echo 'here';
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
							lang='English', 
							state='$state', 
							lga='$lga', 
							network='$network', 
							gender='$gender', 
							data_x_uuid='$datax_uid',
							call_time='$callStartTime' WHERE sessionId='$sessionId' AND caller='$callerNumber'");
                        // $dataQ = mysqli_query($data_x_db, "UPDATE `contacts` SET sent=1 WHERE phone_number='$callerNumber'");
                        // if ($dataQ) {
                        // 	echo 'yes';
                        // }
                    } else {
                        $query = mysqli_query($db, "UPDATE `outbound` SET status='$status', callSessionState='$CSS', callerCountryCode='$CCC', currencyCode='$currencyCode', amount='$amount', durationInSecs='$DIS', lang='English', call_time='$callStartTime' WHERE sessionId='$sessionId' AND caller='$callerNumber'");
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
                        echo 'no';
                    }
                    die;
                }
            }
        }
    }?>