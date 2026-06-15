# Animal Villa
## Design Document

## Contributors :
- Elizabeth Bissinger
- Chloe(Cass) Motz
- Jake Plagge
- Kelli Porter

## Scrum Roles :
- DevOps/Product Owner/Scrum Master: Kelli Porter
- Frontend Developer: Elizabeth Bissinger
- Integration Developer: Jake Plagge, Chloe(Cass) Motz

## Scrum Board :
| Sprint | Goals | Working | Completed |
| --- | --- | --- | --- |
| Sprint #1 | --- | --- | Set up repository|
|| --- | --- | Struggle |
| Sprint #2 | Looper Method |||
|| JSON Files |||
|| Player & Character Methods |||
|| Tutorial Setup |||
| Sprint #3 | More Story |||
|| Some Animation |||
|| Create Settings |||
|| Add Login Method |||

## Meeting Schedule :
- Wednesdays @ 6:00 PM via Microsoft Teams
- Sundays @ Any Time via Microsoft Teams (As Needed)

| Week | Topic | Elizabeth | Cass | Jake | Kelli |
| --- | --- | --- | --- | --- | --- |
| 1 (5/11/22) | Sprint 0 Start | --- | --- | --- | --- |
| 2 (5/18/22) | Forming Groups/Design Doc | Attended | Attended | Attended | Attended |
| 3 (5/25/22)| Sprint 1 Start | Attended | Attended | Attended | Attended |
| 4 (6/1/22)| Story Building and Sudo Code | Attended | Attended | Attended | Attended |
| 5 (6/8/22)| info | Attended | Attended | Attended | Attended |
| 6 (6/15/22)| info | Excused | Attended | Excused | Attended |
| 7 (6/22/22)| Sprint 2 Start | Absent | Attended | Attended | Attended |
| 8 (6/29/22)| info | Attended | Attended | Attended | Attended |
| 9 (7/6/22) | info | Attended | Attended | Attended | Attended |
| 10 (7/13/22) | info | --- | --- | --- | --- |
| 11 (7/20/22) | Sprint 3 Start | --- | --- | --- | --- |
| 12 (7/27/22) | info | --- | --- | --- | --- |
| 13 (8/3/22) | info | --- | --- | --- | --- |
| 14 (8/10/22) | info | --- | --- | --- | --- |
| 15 (8/17/22) | info | --- | --- | --- | --- |

## Introduction :
You have just moved into the town of Animal Villa! Introductions can be hard, but your neighbors seem friendly. Choose how you interact with your fellow animal friends and make meaningful relationships! Beware of creating a bad reputation for yourself, and make sure to keep track of your finances. There are a lot of new faces to see, places to visit, and connections to be made!
- Randomly generated scenarios and multiple endings
- Room for different play styles
- A cast of cutesy animal folk

## Functional Requirements :
### Requirement 1 - Navigating and Making Choices :
#### Scenario :
- As a player, I want to be able to swipe left and right to make quick and easy decisions

#### Dependencies :
- Navigation and choice making will be smooth and consistant
- Choices will have a YES or NO responce

#### Assumptions : 
- Players will be presented with two choices

#### Examples :
1. Making Choices
   - Given two choices on the screen
   - When a player wants to make a choice
   - The player will swipe left for NO and right for YES

### Requirement 2 - Saving Your Progress :
#### Scenario :
- As a player, I want to be able to save my progress

#### Dependencies :
- Saving progress will be automatic
- The game should save to the app or to a signed in account

#### Assumptions : 
- The player does not get to create a manual save

#### Examples :
1. Closing the App
   - Given the player wants to quit the game
   - When a player closes the applicaiton
   - The player's progress will be stored in the application or in an online cloud

### Requirement 3 - Changing Settings :
#### Scenario : 
- As a player, I want to be able to change the settings of the game

#### Dependencies:
- Adjusting the volume of the game
- Changing text speed

#### Assumptions:
- The background music is too loud/soft
- The sound effects are too soft
- The text speed is too slow/fast

#### Examples:
1. Turn On/Off background music
   - Given the ability to adjust the volume to a higher or lower setting
   - When a player wants to adjust the volume
   - The player can easily access the music settings
2. Turn On/Off sound effects
   - Given the ability to adjust the volume to a higher or lower setting
   - When the player wants to adjust the volume
   - The player can easily access the sound effect settings
3. Change the text speed 
   - Given the ability to adjust the text speed of the incoming prompts
   - When a player is having difficulties reading the prompt 
   - The player can easily access the text speed settings

## Class Diagram
![Class Diagram](https://github.com/portekn/Mobile-Device-Programming-Animal-Villa/blob/Design-Document-Draft/Images/Animal%20Villa%20Class%20Diagram%20Final.png)

## Class Diagram Descriptions:
### Interfaces:
   - **Home Screen:** The first screen the user sees. This is the title screen for the game.
   - **Play Area:** This will host the actual game and the screen the user will interact most with.
   - **Character List:** Screen where the player can view information on the characters they have meet.
   - **Storage:**  Screen where the player can see what is in their inventory.
   - **Settings:** Screen where the player can change the settings.
   - **Sign-in:** Screen where the player can sign in to the game.

### Methods:
   - **Start:** Method that starts the game/loads the game from a save.
   - **Sign-in:** Method that signes the user into an account to load their saved progress.
   - **Settings:** Methods that changes the settings of the game.
   - **Art & Sound:** Method that retrieves the art and sound for the app.
   - **Player:** Method that holds player information and allows for interaction with the app.
   - **Character:** Method that holds character information and allows interaction with the app. 
   - **Location:** Method that hodls location information and allows interaction with the app.

### Objects:
   - **Player:** Object that holds information related to the player.
   - **Player Storage:** Object that holds information related to the players inventory.
   - **Character:** Object that holds information related to the characters in the game.
   - **Character Storage:** Object that holds information related to the characters inventory.
   - **Location:** Object that holds information about a location.
   - **Transportation:** Object that holds information about different modes of transportation.
   - **Data:** Object that holds information on all prompts and where they are stored.

## Story Board
![Story Board Gameplay](https://github.com/portekn/Mobile-Device-Programming-Animal-Villa/blob/73444f57bbc2ccc11019ab19554065e46bbda7d6/Images/storyboard%20idea.PNG)
![Story Board Title](https://github.com/portekn/Mobile-Device-Programming-Animal-Villa/blob/73444f57bbc2ccc11019ab19554065e46bbda7d6/Images/storyboard%20title.PNG)

## Endings

The week (Monday → Sunday) ends with one of four ending screens. The ending
played is chosen from the player's final stats after the very last Sunday
prompt, in this priority order:

| Priority | Ending     | Trigger (final stat)                  | What it means                               |
| -------- | ---------- | ------------------------------------- | ------------------------------------------- |
| 1        | Bad ❤️‍🔥     | `Status < 30`                         | The village turns on you (witch trial).     |
| 2        | Exhausted ❤️ | `Status ≥ 30` and `Energy < 30`     | You helped everyone but burned yourself out.|
| 3        | Penniless 💲 | `Status ≥ 30`, `Energy ≥ 30`, `Money < 40` | You're loved, well-rested, and broke.  |
| 4        | Good ✨     | all three above the thresholds        | Animal Villa truly feels like home.         |

After the final ending prompt, pressing **Back to Start** returns you to the
title screen.

### Patterns to reach every ending

All players start the week with `Energy = Money = Status = 50`. Each row
below lists, for the named day, the meaningful choice to pick (the trivial
"Continue." prompts that have no stat change are omitted). `L` = left button,
`R` = right button. Both buttons say the same thing on the wrap-up prompts,
so the choice there doesn't matter.

#### ✨ Good ending — be helpful but rest when you can
| Day       | Choice                                                                 |
| --------- | ---------------------------------------------------------------------- |
| Monday    | `L` *"Sure thing, Cornelius!"*, `R` *"You can count on me, Mrs. Rabbit."*, `L` *"Time to sleep for the night."* |
| Tuesday   | `L` *"Check in on the Fox family."*, `L` *"Stay for lunch with the Foxes."* |
| Wednesday | `R` *"Walk into the forest to help Rosemary."*, `L` *"Take the safe path along the ridge."*, `L` *"Go straight to bed."* |
| Thursday  | `L` *"Of course, let's go to the library."*, `R` *"Ask Keno to save questions for the end."*, `R` *"Politely refuse and head home."*, `L` *"Take an early evening nap."* |
| Friday    | `L` *"Of course, I'd love to help."*, `L` *"Decorate the main stage."*, `R` *"Take a short break at the bakery."*, `R` *"Politely decline the reward."* |
| Saturday  | `R` *"Sample treats at the food stalls."*, `L` *"Thank her warmly and share with passersby."*, `R` *"Wave from the crowd, too shy to go up."*, `R` *"Walk home under the starlight."* |
| Sunday    | `L` *"Get dressed and head to the brunch."*, `L` *"Join them and share stories."*, `L` *"Raise your glass too."*, `L` *"End your week with a peaceful evening."* |

#### 😈 Bad ending — keep picking the cold/selfish option (drives **Status** down)
| Day       | Choice                                                                 |
| --------- | ---------------------------------------------------------------------- |
| Monday    | `R` *"I'm sorry, but I was hoping to get some more sleep"*, `L` *"Time to sleep for the night."* |
| Tuesday   | `L` *"Check in on the Fox family."*, `R` *"Thank her and continue on."* |
| Wednesday | `L` *"Head to the lake and meet Captain Otto."*, `R` *"Sorry, I'm not up for heavy lifting today."*, `L` *"Head home to rest."* |
| Thursday  | `R` *"Sorry, maybe another time."*, `R` *"Spend the day quietly indoors."*  |
| Friday    | `R` *"I'd rather take the day for myself."*, `L` *"Continue to the next day."*  |
| Saturday  | `R` *"Sample treats at the food stalls."*, `R` *"Thank her and keep the treats for yourself."*, `R` *"Wave from the crowd, too shy to go up."*, `R` *"Walk home under the starlight."* |
| Sunday    | `R` *"Spend the morning at home alone."*, `L`/`R` *"End your week resting at home."* |

#### 🥱 Exhausted ending — help everyone with heavy work, never rest (drives **Energy** down)
| Day       | Choice                                                                 |
| --------- | ---------------------------------------------------------------------- |
| Monday    | `L` *"Sure thing, Cornelius!"*, `R` *"You can count on me, Mrs. Rabbit."*, `R` *"Let's check out the village at night!"* |
| Tuesday   | `R` *"Check in on the Crow family."*, `L` *"Of course, Mr. Crow. I'd be happy to help."* |
| Wednesday | `R` *"Walk into the forest to help Rosemary."*, `R` *"Take the muddy creek path for the rarer herbs."*, `R` *"Visit the village square first."* |
| Thursday  | `L` *"Of course, let's go to the library."*, `L` *"Patiently answer every question."*, `L` *"Accept the warm pie."*, `R` *"Write a short letter to a friend back home."* |
| Friday    | `L` *"Of course, I'd love to help."*, `R` *"Run supplies between food stalls."*, `L` *"Keep working until everything is done."*, `R` *"Politely decline the reward."* |
| Saturday  | `L` *"Try your luck at the festival games."*, `R` *"Give the prize ticket to Keno."*, `L` *"Step on stage and say a few words."*, `L` *"Stay until the very last lantern is lit."* |
| Sunday    | `L` *"Get dressed and head to the brunch."*, `L`, `L`, `L` *"End your week with a peaceful evening."* |

#### 💸 Penniless ending — spend money whenever you can, stay friendly (drives **Money** down while keeping Status & Energy up)
| Day       | Choice                                                                 |
| --------- | ---------------------------------------------------------------------- |
| Monday    | `L` *"Sure thing, Cornelius!"*, `L` *"I'd like to stay and help, but I have to get back…"*, `L` *"Time to sleep for the night."* |
| Tuesday   | `L` *"Check in on the Fox family."*, `L` *"Stay for lunch with the Foxes."* |
| Wednesday | `R` *"Walk into the forest to help Rosemary."*, `L` *"Take the safe path along the ridge."*, `L` *"Go straight to bed."* |
| Thursday  | `R` *"Sorry, maybe another time."*, `L` *"Try to make up for it later."* &nbsp;(spends Money) |
| Friday    | `L` *"Of course, I'd love to help."*, `L` *"Decorate the main stage."*, `R` *"Take a short break at the bakery."* &nbsp;(spends Money), `R` *"Politely decline the reward."* &nbsp;(no money gain) |
| Saturday  | `R` *"Sample treats at the food stalls."* &nbsp;(spends Money), `R` *"Thank her and keep the treats for yourself."*, `L` *"Step on stage and say a few words."*, `R` *"Walk home under the starlight."* |
| Sunday    | `L`, `L`, `L`, `L` *"End your week with a peaceful evening."* |

> 💡 Tip: if you want to confirm which ending you triggered, watch the
> three bottom stat tiles on Sunday's last prompt — they show your final
> Energy ❤️, Status 🔥, and Money 💲 right before the ending plays.
