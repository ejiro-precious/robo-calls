<?php
/**
 * Created by IntelliJ IDEA.
 * User: ejiro
 * Date: 6/12/18
 * Time: 12:32 AM
 */
	require('db.php');
	require('vendor/autoload.php');
    use GuzzleHttp\Client;

	if (isset($_POST['direction']) && isset($_POST['callSessionState'])) {
        if($_POST['direction'] == "Inbound" && $_POST['callSessionState'] == "Completed") {
            $caller = $_POST['callerNumber'];
            $callee = $_POST['destinationNumber'];
            // var_dump($_POST['callerNumber']);
            $headers = [
                "apikey" => "6d8bc82ca00a8a1baa7509ef143c3284682f5b28524b273fd3341d516f39e77f",
                "Accept" => "application/json",
                "Content-type" => "application/x-www-form-urlencoded"
            ];
            $client = new Client([
                'base_uri' => 'https://voice.africastalking.com',
                'curl' => [
                    CURLOPT_SSL_VERIFYPEER => false
                ]
            ]);
            $query = [
                "from" => $callee,
                "to" => $caller,
                "username" => "audioads"
            ];
            $body = http_build_query($query, '', '&');
            $response = $client->request('POST', '/call', [
                'form_params' => $query,
                'headers' => $headers
            ]);

            $date = date("Y-m-d h:i:sa");
            $results = json_decode($response->getBody(), true);var_dump($results);
            $resultError = $results['errorMessage'];
            $entries = $results['entries'];
            if ($resultError == "None" && $entries !== NULL) {
                for ($i=0; $i < count($entries); $i++) {
                    $entry = $entries[$i];
                    $caller = $entry['phoneNumber'];
                    $sessionId = $entry['sessionId'];
                    $status = $entry['status'];
                    $query = mysqli_query($db, "INSERT INTO `outbound` 
                    ( sessionId, caller, status, callee, call_time ) 
                    VALUES ( '$sessionId', '$caller', '$status', '$callee', '$date'  )");
                }
                echo "yes";
            }
        }
    }
?>