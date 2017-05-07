## Smart Fire Detector - CSE 334 Microprocessors Course Term Project  
##### TEACHER: Asst. Prof. Alp Arslan Bayrakçı

This repo includes simple client for Smart Fire System. When client connects system, System sends informations to phone with serial communication wia **HC06 - Bluetooth** module. 

In dangerous states, system will send alert to phone. User can control all system for example: open/close fan, active/deactive system, send informations to LCD display...

### TODO
- [x] Bluetooth connection on SCI1, PS3-2 pins 
- [x] Send number to leds
- [ ] Listen HCS12 with thread for important messages
- [ ] Activate / Deactivate system components
- [ ] Real time buzzer
