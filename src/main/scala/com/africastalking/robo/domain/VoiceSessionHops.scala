package com.africastalking.robo
package domain

case class VoiceSessionHops(
    amount: String,
    sessionId: String,
    direction: String,
    destinationNumber: String,
    status: String,
    durationInSeconds: String,
    callSessionState: String,
    callerNumber: String
)

