#!/bin/bash
Xvfb :99 &
export DISPLAY=:99
echo "Display variable: $DISPLAY"
echo "Executing booking for Leo..."
java -jar /home/leonardo/development/workspace/GymBooker/target/GymBooker-1.0-SNAPSHOT.jar autojob leo > /home/leonardo/gymbooker.log 2>&1
echo "Executing booking for Clau..."
java -jar /home/leonardo/development/workspace/GymBooker/target/GymBooker-1.0-SNAPSHOT.jar autojob clau >> /home/leonardo/gymbooker.log 2>&1
echo "End."
