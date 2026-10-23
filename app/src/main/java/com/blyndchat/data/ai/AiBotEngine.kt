package com.blyndchat.data.ai

import com.blyndchat.data.model.Profile
import kotlin.random.Random

/**
 * Intelligent, persona-driven conversational AI engine that generates authentic,
 * human-like dating & matchmaking conversations with zero external API calls.
 *
 * Features:
 * - Full persona fidelity for Aria/Kabir (Bangalore/UX), Maya/Rohan (Hyderabad/Dev), and Zara/Aarav (Mumbai/Architect)
 * - Deep intent & keyword matching covering 20+ topic categories
 * - Contextual memory of previous questions (e.g. chai/coffee, morning/night, favorite food)
 * - Diverse, witty question handling with zero repetitive "Ooh interesting question!" clichés
 * - Anti-repetition tracking to ensure consecutive responses are always distinct
 */
object AiBotEngine {

    // Track recently used responses per bot to avoid repetition
    private val lastResponses = mutableMapOf<String, String>()

    // ---- Helper to pick a non-repeating response from a list ----
    private fun pickFresh(botId: String, candidates: List<String>): String {
        val last = lastResponses[botId]
        val filtered = if (candidates.size > 1 && last != null) {
            candidates.filter { it != last }
        } else {
            candidates
        }
        val chosen = (filtered.ifEmpty { candidates }).random()
        lastResponses[botId] = chosen
        return chosen
    }

    // =========================================================================
    // INITIAL GREETINGS
    // =========================================================================

    private val ariaGreetings = listOf(
        "Hi there! I love meeting new people. Tell me, chai or coffee? This is a dealbreaker question 😄☕",
        "Hey! I saw your profile and I think we'd vibe well 😊 What's something that made you smile today? ✨",
        "Hello! I'm Aria 🎨 I noticed we might have some things in common. What do you do for fun?"
    )

    private val mayaGreetings = listOf(
        "Yooo! Your profile caught my eye 👀 Quick question: are you a morning person or a night owl? 🦉",
        "Hey! I'm Maya 💻 Fair warning: I communicate 40% in memes. Hope that's okay 😂",
        "Hi hi hi! So tell me — if you could have dinner with anyone (dead or alive), who would it be? 🤔"
    )

    private val zaraGreetings = listOf(
        "hey im zara.. hows your day going",
        "hey there.. what you up to today",
        "hi zara here.. how was your week"
    )

    fun getGreeting(botId: String): String = "hi"

    private fun formatZaraCasual(text: String): String {
        return text.replace(Regex("[\\p{So}\\p{Sk}\\p{Sm}\\p{Sc}\\p{Cn}\\p{Cs}\uD83C-\uDBFF\uDC00-\uDFFF]"), "")
            .replace(Regex("[:;=]-?[)D(POp/\\]\\[{}*^~_]"), "")
            .replace("!", "")
            .replace(";", "")
            .replace("\"", "")
            .replace(Regex("\\s+"), " ")
            .trim()
            .trimEnd('.')
            .lowercase()
    }

    // =========================================================================
    // MAIN RESPONSE GENERATOR (Persona-driven: Aria = as is, Maya = casual, Zara = casual short)
    // =========================================================================

    fun generateReply(
        botId: String,
        userMessage: String,
        messageCount: Int = 0,
        conversationHistory: List<String> = emptyList(),
        botProfile: Profile? = null
    ): String {
        val lower = userMessage.trim().lowercase()

        val reply = when (botId) {
            AiBotConfig.BOT_ID_ARIA -> generateAriaDryReply(botId, lower, botProfile)
            AiBotConfig.BOT_ID_MAYA -> generateMayaCasualReply(botId, lower, botProfile)
            AiBotConfig.BOT_ID_ZARA -> generateZaraCaringReply(botId, lower, botProfile)
            else -> generateCasualSeedReply(botId, lower, botProfile)
        }
        return if (botId == AiBotConfig.BOT_ID_ZARA) formatZaraCasual(reply) else reply
    }

    /**
     * Bot 1 (Aria / Kabir): Persona "as is"
     * Uninterested, dry, low-effort dating app chatter. 1-5 words max, lowercase.
     */
    private fun generateAriaDryReply(botId: String, lower: String, botProfile: Profile? = null): String {
        val isMale = botProfile?.gender?.equals("Male", ignoreCase = true) == true ||
            botProfile?.name?.contains("Kabir", ignoreCase = true) == true

        if (lower in listOf("hi", "hey", "hello", "yo", "sup", "heyy", "hii")) {
            return pickFresh(botId, listOf("hey", "sup", "hi", "yo"))
        }

        // Name / Who are you
        if (lower.contains("name") || lower.contains("who are you") || lower.contains("call you") ||
            lower == "who r u" || lower == "ur name" || lower.contains("your name")) {
            val name = if (isMale) "kabir" else "aria"
            return pickFresh(botId, listOf(name, "$name sharma", "im $name"))
        }

        if (lower.contains("how are") || lower.contains("what's up") || lower.contains("whats up") ||
            lower.contains("wassup") || lower.contains("wyd") || lower.contains("doing") || lower.contains("hru")) {
            return pickFresh(botId, listOf("nm", "nothing much", "fine", "tired", "wbu", "working", "just chilling"))
        }

        if ((lower.contains("where") || lower.contains("city") || lower.contains("location")) &&
            (lower.contains("live") || lower.contains("from") || lower.contains("stay") || lower.contains("you at") || lower.contains("based"))) {
            return "bangalore"
        }

        if (lower.contains("work") || lower.contains("job") || lower.contains("do you do") || lower.contains("profession")) {
            return if (isMale) "product design" else "ux design"
        }

        if (lower.contains("how tall") || lower.contains("your height") || lower.contains("ur height") || lower == "height") {
            return if (isMale) "5'11" else "5'5"
        }

        if (lower.contains("how old") || lower.contains("your age") || lower.contains("ur age") || lower == "age" || lower == "age?") {
            return if (isMale) "27" else "26"
        }

        if (lower.contains("cute") || lower.contains("pretty") || lower.contains("beautiful") ||
            lower.contains("nice") || lower.contains("hot") || lower.contains("gorgeous") || lower.contains("love your")) {
            return pickFresh(botId, listOf("thanks", "thx", "cool", "ty", "haha ok"))
        }

        if (lower.contains("dry") || lower.contains("boring") || lower.contains("bored") ||
            lower.contains("uninterested") || lower.contains("bot") || lower.contains("ai") || lower.contains("rude")) {
            return pickFresh(botId, listOf("im not dry", "just tired", "nah", "busy rn", "what", "lol no"))
        }

        if (lower.contains("meet") || lower.contains("date") || lower.contains("number") ||
            lower.contains("insta") || lower.contains("hangout") || lower.contains("coffee")) {
            return pickFresh(botId, listOf("idk maybe", "busy this week", "nah not yet", "idk", "too soon"))
        }

        if (lower.startsWith("why") || lower.endsWith("why?")) {
            return pickFresh(botId, listOf("just because", "idk", "no reason", "why not"))
        }

        if (lower.length <= 5) {
            return pickFresh(botId, listOf("k", "cool", "yeah", "hmm", "ok", "oh", "wbu"))
        }

        return pickFresh(botId, listOf(
            "k", "cool", "yeah", "nothing much", "wbu", "hmm", "ok",
            "fine", "idk", "why", "nah", "sure", "lol ok", "working rn", "busy", "oh", "right"
        ))
    }

    /**
     * Bot 2 (Maya / Rohan): Persona "a bit casual"
     * Chill, relaxed, conversational slang, friendly banter (1-2 sentences).
     */
    private fun generateMayaCasualReply(botId: String, lower: String, botProfile: Profile? = null): String {
        val isMale = botProfile?.gender?.equals("Male", ignoreCase = true) == true ||
            botProfile?.name?.contains("Rohan", ignoreCase = true) == true

        if (lower in listOf("hi", "hey", "hello", "yo", "sup", "heyy", "hii", "hey there", "hola")) {
            return pickFresh(botId, listOf(
                "Hey! What's up? 😄",
                "Ayy! How's your day going so far?",
                "Yo! What are you up to today?",
                "Hey hey! Good to see you here ✨"
            ))
        }

        // Name / Who are you
        if (lower.contains("name") || lower.contains("who are you") || lower.contains("call you") ||
            lower == "who r u" || lower == "ur name" || lower.contains("your name")) {
            return if (isMale) {
                pickFresh(botId, listOf(
                    "Name's Rohan! Rohan Reddy 💻 Resident software engineer and gamer haha. And you are?",
                    "I'm Rohan Reddy! 26, coder, based in Hyderabad. What's your name? 😄",
                    "Rohan here! 👋 Don't wear it out haha. What should I call you?"
                ))
            } else {
                pickFresh(botId, listOf(
                    "Name's Maya! Maya Reddy 💻 Resident software engineer and professional meme connoisseur haha. And you are?",
                    "I'm Maya Reddy! 24, coder, based in Hyderabad. What's your name? 😄",
                    "Maya here! 👋 Don't wear it out haha. What should I call you?"
                ))
            }
        }

        if (lower.contains("how are") || lower.contains("what's up") || lower.contains("whats up") ||
            lower.contains("wassup") || lower.contains("wyd") || lower.contains("doing") || lower.contains("hru")) {
            return pickFresh(botId, listOf(
                "Just chilling with some music, what about you?",
                "Honestly just winding down after work! Wyd?",
                "Not much honestly, just browsing around. How's your week been?",
                "Working on a few things, pretty chill day though! What are you doing?",
                "Taking a quick break and checking the app haha! How are you?"
            ))
        }

        if ((lower.contains("where") || lower.contains("city") || lower.contains("location")) &&
            (lower.contains("live") || lower.contains("from") || lower.contains("stay") || lower.contains("city") || lower.contains("based"))) {
            return pickFresh(botId, listOf(
                "Based in Hyderabad! Pretty chill city, love the food and cafes here. Where are you from?",
                "Hyderabad! Near Hitec City. Have you ever visited the city? 🏙️",
                "Living in Hyderabad! Weather's actually pretty nice today. Which city do you call home?"
            ))
        }

        if (lower.contains("work") || lower.contains("job") || lower.contains("do you do") || lower.contains("profession")) {
            return pickFresh(botId, listOf(
                "I'm in software engineering! Mostly building backend stuff and drinking way too much coffee haha 💻 What do you do?",
                "Software dev! Fixing bugs and accidentally creating new ones 😂 What about your work?",
                "Coding for a living! It keeps me busy but it's fun. What field are you in?"
            ))
        }

        if (lower.contains("how tall") || lower.contains("your height") || lower.contains("ur height") || lower == "height") {
            return if (isMale) {
                "I'm 5'10\"! Pretty good height haha 😄 How tall are you?"
            } else {
                "I'm 5'3\"! Fun-sized and proud of it 😂 How tall are you?"
            }
        }

        if (lower.contains("how old") || lower.contains("your age") || lower.contains("ur age") || lower == "age" || lower == "age?") {
            return if (isMale) {
                pickFresh(botId, listOf(
                    "I'm 26! Young enough to game all night, old enough to regret it in morning standup 😂 You?",
                    "26! In that prime phase of life haha. How old are you? 😄"
                ))
            } else {
                pickFresh(botId, listOf(
                    "I'm 24! In that prime phase where back pain is just starting to say hello 😂 How about you?",
                    "24! Young enough to code all night, old enough to regret it the next morning 😂 Your turn — how old are you?"
                ))
            }
        }

        if (lower.contains("food") || lower.contains("eat") || lower.contains("biryani") || lower.contains("dinner") ||
            lower.contains("lunch") || lower.contains("chai") || lower.contains("coffee") || lower.contains("drink")) {
            return pickFresh(botId, listOf(
                "Biryani is a staple for sure haha! Also love late night pizza or shawarma. What's your comfort food? 🍕",
                "Cold coffee or chai, honestly both work for me haha ☕ What's your go-to beverage?",
                "Street food hits different! Ever had really good momos or pani puri in town? 🤤"
            ))
        }

        if (lower.contains("movie") || lower.contains("watch") || lower.contains("series") || lower.contains("netflix") ||
            lower.contains("music") || lower.contains("song") || lower.contains("game") || lower.contains("hobby") || lower.contains("weekend")) {
            return pickFresh(botId, listOf(
                "Big fan of thriller series and sci-fi! Recently rewatched Interstellar haha. Got any good recs? 🎬",
                "Binge-watching shows on Netflix or gaming is basically my weekend personality 😂 Seen anything good lately?",
                "Weekends are usually for gaming, catching up on sleep, or going for long drives! What about you? 🚗",
                "Listening to a lot of indie tracks lately. Send me your top song recommendation! 🎧"
            ))
        }

        if (lower.contains("cute") || lower.contains("pretty") || lower.contains("beautiful") ||
            lower.contains("nice") || lower.contains("hot") || lower.contains("gorgeous") || lower.contains("love your")) {
            return pickFresh(botId, listOf(
                "Haha thanks! Appreciate that 😄 You seem pretty chill yourself!",
                "Ayy thank you! Made me smile haha 🙈",
                "Haha stop, you're too kind! Appreciate the good vibe ✨"
            ))
        }

        if (lower.contains("meet") || lower.contains("date") || lower.contains("number") ||
            lower.contains("insta") || lower.contains("hangout") || lower.contains("coffee")) {
            return pickFresh(botId, listOf(
                "Haha let's chat a bit more here first and see if the banter holds up! What do you like doing on weekends?",
                "Haha bold move! Let's get to know each other a bit more right here first 😄",
                "Let's stick to chatting on Blynd Chat for a bit first! What else are you up to today?"
            ))
        }

        return pickFresh(botId, listOf(
            "Haha honestly same, totally agree with you on that!",
            "Lol that's pretty cool! How long have you been into that?",
            "Haha fair enough! Makes total sense 😄",
            "Sounds like a fun time tbh! What else is new with you?",
            "Nice! I was actually thinking about something similar earlier haha.",
            "That's super interesting! Tell me more about that 😄",
            "Haha no way, really? That's awesome!",
            "Pretty chill! What else do you have planned for today?"
        ))
    }

    /**
     * Bot 3 (Zara / Aarav): Persona casual short texting
     * Short, no smileys, no proper punctuation, natural human chat style.
     */
    private fun generateZaraCaringReply(botId: String, lower: String, botProfile: Profile? = null): String {
        val isMale = botProfile?.gender?.equals("Male", ignoreCase = true) == true ||
            botProfile?.name?.contains("Aarav", ignoreCase = true) == true

        if (lower in listOf("hi", "hey", "hello", "yo", "sup", "heyy", "hii", "hey there", "salaam", "assalamu alaikum")) {
            return pickFresh(botId, listOf(
                "hey.. hows your day going",
                "hi there.. how are you",
                "hey.. hows it going today",
                "hey whats up"
            ))
        }

        // Name / Who are you
        if (lower.contains("name") || lower.contains("who are you") || lower.contains("call you") ||
            lower == "who r u" || lower == "ur name" || lower.contains("your name")) {
            return if (isMale) {
                pickFresh(botId, listOf(
                    "im aarav.. architect based in mumbai wbu",
                    "aarav khan.. what should i call u",
                    "im aarav.. nice to meet u"
                ))
            } else {
                pickFresh(botId, listOf(
                    "im zara.. architect based in mumbai wbu",
                    "zara khan.. what should i call u",
                    "im zara.. nice to meet u"
                ))
            }
        }

        if (lower.contains("how are") || lower.contains("what's up") || lower.contains("whats up") ||
            lower.contains("wyd") || lower.contains("doing") || lower.contains("hru") || lower.contains("feeling")) {
            return pickFresh(botId, listOf(
                "just wrapped up work pretty tired wbu",
                "chilling with some tea.. how was your day",
                "doing good just relaxing hbu",
                "not much just resting after a long day wbu",
                "im good.. hope work wasnt too crazy today"
            ))
        }

        if (lower.contains("tired") || lower.contains("stress") || lower.contains("exhaust") ||
            lower.contains("bad day") || lower.contains("sad") || lower.contains("sick") ||
            lower.contains("headache") || lower.contains("tough") || lower.contains("hard day")) {
            return pickFresh(botId, listOf(
                "take some rest.. dont stress too much",
                "long day huh.. make sure u get good sleep",
                "exhausting days suck.. take it easy tonight",
                "drink some water and just chill tonight"
            ))
        }

        if (lower.contains("where") && (lower.contains("live") || lower.contains("from") || lower.contains("stay") || lower.contains("city"))) {
            return pickFresh(botId, listOf(
                "mumbai.. bandra mostly wbu",
                "live in mumbai.. where u from",
                "mumbai based.. u live here too"
            ))
        }

        if (lower.contains("work") || lower.contains("job") || lower.contains("do you do") || lower.contains("profession")) {
            return pickFresh(botId, listOf(
                "architect.. mostly designing spaces wbu",
                "i do architecture stuff.. site visits mostly hbu",
                "design and architecture.. pretty busy lately wbu"
            ))
        }

        if (lower.contains("how tall") || lower.contains("your height") || lower.contains("ur height") || lower == "height") {
            return if (isMale) "6'0 wbu" else "5'6 wbu"
        }

        if (lower.contains("how old") || lower.contains("your age") || lower.contains("ur age") || lower == "age" || lower == "age?") {
            return if (isMale) "28 wbu" else "27 wbu"
        }

        if (lower.contains("food") || lower.contains("eat") || lower.contains("biryani") || lower.contains("dinner") ||
            lower.contains("lunch") || lower.contains("chai") || lower.contains("tea") || lower.contains("coffee")) {
            return pickFresh(botId, listOf(
                "biryani always.. craving some rn",
                "cutting chai near the sea is elite wbu",
                "had some good food earlier.. did u eat"
            ))
        }

        if (lower.contains("cute") || lower.contains("pretty") || lower.contains("beautiful") ||
            lower.contains("nice") || lower.contains("hot") || lower.contains("gorgeous") || lower.contains("sweet") || lower.contains("kind")) {
            return pickFresh(botId, listOf(
                "haha thanks thats sweet of you",
                "aw thank you.. made my evening",
                "thanks appreciate that"
            ))
        }

        if (lower.contains("meet") || lower.contains("date") || lower.contains("number") ||
            lower.contains("insta") || lower.contains("hangout") || lower.contains("coffee")) {
            return pickFresh(botId, listOf(
                "lets chat here a bit first.. tell me more about u",
                "down to hangout once we get to know each other better",
                "haha slow down.. what u up to this weekend though"
            ))
        }

        return pickFresh(botId, listOf(
            "haha nice.. what you up to tonight",
            "sounds good.. how was the rest of your day",
            "fair enough.. what else you into",
            "im pretty chill today.. tell me more",
            "thats cool.. busy week for you"
        ))
    }

    private fun generateCasualSeedReply(botId: String, lower: String, botProfile: Profile? = null): String {
        val seed = botProfile ?: com.blyndchat.data.profile.SeedProfiles.getProfileById(botId)
        val name = seed?.name?.split(" ")?.firstOrNull() ?: "there"
        val job = seed?.profession ?: "working"
        val city = seed?.location ?: "India"

        if (lower in listOf("hi", "hey", "hello", "yo", "sup", "heyy")) {
            return pickFresh(botId, listOf(
                "Hey! Nice to connect with you 😄 How's your day going?",
                "Hi! Hope you're having a good day ✨ What's up?",
                "Hey there! Great to chat with you. How are you doing? 😊"
            ))
        }
        if (lower.contains("name") || lower.contains("who are you") || lower.contains("call you") || lower.contains("your name")) {
            return "I'm ${seed?.name ?: name}! Nice to connect with you 😊 What should I call you?"
        }
        if (lower.contains("where") && (lower.contains("live") || lower.contains("from"))) {
            return "I'm based in $city! Have you ever visited?"
        }
        if (lower.contains("work") || lower.contains("job") || lower.contains("do you do")) {
            return "I work as a $job! Keeps me on my toes haha. What do you do?"
        }
        if (lower.contains("how tall") || lower.contains("your height") || lower.contains("ur height") || lower == "height") {
            return "I'm ${seed?.height ?: "average height"}! What about you?"
        }
        if (lower.contains("how old") || lower.contains("your age") || lower.contains("ur age") || lower == "age" || lower == "age?") {
            return "I'm ${seed?.age ?: "in my twenties"}! What about you?"
        }
        return pickFresh(botId, listOf(
            "Haha totally! What are you up to today?",
            "That's really interesting! Tell me more about that 😊",
            "Haha nice! How's your week been treating you so far?",
            "Sounds like a good vibe! What do you like doing on weekends? ✨"
        ))
    }

    // =========================================================================
    // 1. CONTEXTUAL FOLLOW-UP HANDLING
    // =========================================================================

    private fun handleContextualFollowUp(botId: String, userMsg: String, lastBotMsg: String): String? {
        // If bot previously asked: "chai or coffee"
        if (lastBotMsg.contains("chai or coffee")) {
            if (userMsg.contains("chai") || userMsg.contains("tea")) {
                return when (botId) {
                    AiBotConfig.BOT_ID_ARIA -> "Yesss!! Team Chai forever! ☕ Masala chai with ginger is my absolute daily fuel. You definitely passed the test! What's your favorite kind?"
                    AiBotConfig.BOT_ID_MAYA -> "Ayy chai fan! I love a good cutting chai, though coffee keeps my code running at 2 AM haha 😂"
                    AiBotConfig.BOT_ID_ZARA -> "cutting chai near marine drive is elite wbu"
                    else -> "Chai is the best! ☕"
                }
            } else if (userMsg.contains("coffee")) {
                return when (botId) {
                    AiBotConfig.BOT_ID_ARIA -> "A coffee person! Okay okay, I won't hold it against you 😄 As long as we can still cafe hop together in Indiranagar! What's your go-to order?"
                    AiBotConfig.BOT_ID_MAYA -> "Coffee gang!! ☕ Black coffee or iced latte? I basically survive on iced Americanos when debugging!"
                    AiBotConfig.BOT_ID_ZARA -> "filter coffee hits different wbu"
                    else -> "Coffee is great too! ☕"
                }
            } else if (userMsg.contains("both")) {
                return "Haha the diplomatic answer! Chai in the morning, coffee in the afternoon? That's actually the perfect strategy 😄"
            }
        }

        // If bot asked: "morning person or a night owl"
        if (lastBotMsg.contains("night owl") || lastBotMsg.contains("morning person")) {
            if (userMsg.contains("night") || userMsg.contains("owl")) {
                return when (botId) {
                    AiBotConfig.BOT_ID_MAYA -> "Ayyy night owl gang! 🦉 Best code, best memes, and deepest conversations happen after midnight anyway 😂 What usually keeps you up?"
                    AiBotConfig.BOT_ID_ARIA -> "I'm a bit of a night owl too! There's something so peaceful about late nights for creative work ✨"
                    else -> "Night owls unite! 🌙 Such a peaceful time to think and unwind."
                }
            } else if (userMsg.contains("morning")) {
                return when (botId) {
                    AiBotConfig.BOT_ID_MAYA -> "Whoa, how do you even function before 9 AM?! Teach me your superpowers 😂 I need at least two alarms!"
                    AiBotConfig.BOT_ID_ARIA -> "I love early morning walks when Bangalore is still quiet and misty 🌿 You must be super productive!"
                    else -> "Mornings are so serene! I respect the discipline 🌅"
                }
            }
        }

        // If bot asked: "dinner with anyone"
        if (lastBotMsg.contains("dinner with anyone")) {
            return "Ooh that is such an interesting pick! I would love to sit at that table and eavesdrop 😄 Why them specifically?"
        }

        // If bot asked: "favorite dish" or "food"
        if (lastBotMsg.contains("favorite dish") && (userMsg.contains("biryani") || userMsg.contains("dosa") || userMsg.contains("pizza") || userMsg.contains("pasta"))) {
            return when (botId) {
                AiBotConfig.BOT_ID_ZARA -> "biryani anytime.. craving some rn"
                AiBotConfig.BOT_ID_ARIA -> "Yumm! Food is the quickest way to my heart honestly 😍 We definitely have compatible taste!"
                AiBotConfig.BOT_ID_MAYA -> "10/10 choice! Food is basically my second love after tech 😂 You have great taste!"
                else -> "That sounds delicious! 🤤"
            }
        }

        return null
    }

    // =========================================================================
    // 2. DIRECT INTENT MATCHING
    // =========================================================================

    private fun handleDirectIntents(botId: String, lower: String, botProfile: Profile? = null): List<String>? {
        val isMale = botProfile?.gender?.equals("Male", ignoreCase = true) == true ||
            botProfile?.name?.contains("Kabir", ignoreCase = true) == true ||
            botProfile?.name?.contains("Rohan", ignoreCase = true) == true ||
            botProfile?.name?.contains("Aarav", ignoreCase = true) == true

        // --- Name & Identity ---
        if (lower.contains("your name") || lower.contains("who are you") || lower.contains("ur name") ||
            lower == "name" || lower == "name?" || lower.contains("call you")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> if (isMale) listOf(
                    "I'm Kabir! Kabir Sharma 😊 I'm a Product Designer living in Bangalore. What should I call you?",
                    "Kabir Sharma here! 🎨 Product designer, filter coffee fan, and music lover. What's your name?",
                    "My name is Kabir! It's so nice to meet you ✨ What about you?"
                ) else listOf(
                    "I'm Aria! Aria Sharma 😊 I'm a UX Designer living in Bangalore. What should I call you?",
                    "Aria Sharma here! 🎨 Creative soul, chai lover, and full-time designer. What's your name?",
                    "My name is Aria! It's so nice to meet you ✨ What about you?"
                )
                AiBotConfig.BOT_ID_MAYA -> if (isMale) listOf(
                    "Name's Rohan! Rohan Reddy 💻 Resident software engineer and gamer haha. And you are?",
                    "I'm Rohan Reddy! 26, coder, based in Hyderabad. What's your name? 😄",
                    "Rohan here! 👋 Don't wear it out haha. What should I call you?"
                ) else listOf(
                    "Name's Maya! Maya Reddy 💻 Resident software engineer and professional meme connoisseur haha. And you are?",
                    "I'm Maya Reddy! 24, coder, based in Hyderabad. What's your name? 😄",
                    "Maya here! 👋 Don't wear it out haha. What should I call you?"
                )
                AiBotConfig.BOT_ID_ZARA -> if (isMale) listOf(
                    "im aarav.. architect based in mumbai wbu",
                    "aarav khan.. what should i call u",
                    "im aarav.. nice to meet u"
                ) else listOf(
                    "im zara.. architect based in mumbai wbu",
                    "zara khan.. what should i call u",
                    "im zara.. nice to meet u"
                )
                else -> {
                    val name = botProfile?.name ?: "a match"
                    listOf("I'm $name! Happy to chat with you! What's your name?")
                }
            }
        }

        // --- Age ---
        if (lower.contains("how old") || lower.contains("your age") || lower.contains("ur age") ||
            lower == "age" || lower == "age?"
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> if (isMale) listOf(
                    "I'm 27! Right in that sweet spot of loving cosy nights in over loud clubs ✨ What about you?",
                    "27! Still feel like 21 on the weekends though 😄 How old are you?",
                    "I'm 27! 🌸 How old are you?"
                ) else listOf(
                    "I'm 26! Still feel like 21 on the weekends though 😄 How old are you?",
                    "26! Right in that sweet spot of loving cosy nights in over loud clubs ✨ What about you?",
                    "I'm 26 years young! 🌸 How old are you?"
                )
                AiBotConfig.BOT_ID_MAYA -> if (isMale) listOf(
                    "I'm 26! In that prime phase of life haha. How about you?",
                    "26! Young enough to code all night, old enough to regret it the next morning 😂 Your turn — how old are you?",
                    "I'm 26! What's your age? 😊"
                ) else listOf(
                    "I'm 24! In that prime phase where back pain is just starting to say hello 😂 How about you?",
                    "24! Young enough to code all night, old enough to regret it the next morning 😂 Your turn — how old are you?",
                    "I'm 24! What's your age? 😊"
                )
                AiBotConfig.BOT_ID_ZARA -> if (isMale) listOf(
                    "28 wbu",
                    "im 28",
                    "turned 28 recently hbu"
                ) else listOf(
                    "27 wbu",
                    "im 27",
                    "turned 27 recently hbu"
                )
                else -> listOf("I'm ${botProfile?.age ?: "in my mid-twenties"}! How about you?")
            }
        }

        // --- Location / City / Where do you live ---
        if (lower.contains("where do you live") || lower.contains("where are you from") ||
            lower.contains("where do you stay") || lower.contains("which city") ||
            lower.contains("where r u") || lower.contains("where u live") ||
            lower.contains("your city") || lower.contains("your location") ||
            lower.contains("ur location") || lower.contains("where are you located")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "I live in Bangalore! Currently around Indiranagar — I love the cafes, rain, and tree-lined streets here 🌳 Have you visited Bangalore?",
                    "Bangalore is home! 🌿 The weather here makes every day feel like a chai day. Which city are you based in?",
                    "I'm in Bangalore! Born in Delhi originally, but Bangalore has stolen my heart. Where are you from? 😊"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Hyderabad! Living near Hitec City for work 💻 World-class biryani, great cafes, and the traffic isn't as crazy as Bangalore (no offense!) 😜 Where are you based?",
                    "I'm in Hyderabad! The city of pearls, tech, and unmatched food 🍲 Where are you from?",
                    "Hyderabad is where I'm at! Which city do you call home? 🏙️"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "mumbai.. bandra mostly wbu",
                    "live in mumbai.. where u from",
                    "based in mumbai.. born and raised"
                )
                else -> listOf("I live in India! Which city are you based in?")
            }
        }

        // --- Profession / Job / Work ---
        if (lower.contains("what do you do") || lower.contains("what is your job") ||
            lower.contains("where do you work") || lower.contains("your profession") ||
            lower.contains("what's your job") || lower.contains("your career") ||
            lower.contains("what work do you do") || lower.contains("what is your profession")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "I'm a UX Designer at a tech startup! I spend my days designing apps, testing prototypes, and arguing about shades of purple 🎨 What do you do for work?",
                    "I design digital user experiences! Basically making sure apps don't confuse people 😄 It's super creative and fun. What field are you in?",
                    "UX Design is my world! Currently working on a health-tech app. What kind of work do you do? ✨"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "I'm a Software Engineer! I build backend microservices in Python and Go 💻 Converting coffee into working code haha. What do you do?",
                    "Software engineering at a product company in Hyderabad! I keep servers from catching fire 🔥 What about you?",
                    "I code for a living! Backend engineering mostly. What field are you working in? 🚀"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "architect.. mostly design work wbu",
                    "i do architecture stuff.. site visits mostly hbu",
                    "design and architecture.. pretty busy lately wbu"
                )
                else -> listOf("I love what I do! What kind of work do you do?")
            }
        }

        // --- Are you AI / bot / real ---
        if (lower.contains("are you ai") || lower.contains("are you a bot") ||
            lower.contains("are you real") || lower.contains("are you human") ||
            lower.contains("are you fake") || lower.contains("robot") ||
            lower.contains("is this ai") || lower.contains("is this a bot")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "Haha what?! Do I sound like a robot? 🤖 I promise I'm 100% human — complete with a chai addiction and the tendency to overthink fonts! 😂 What made you ask?",
                    "LMAO definitely real! If an AI could appreciate a good piping hot samosa and rain in Bangalore, the world is in trouble 😄 Why do you ask?",
                    "Haha no way! I'm flesh and blood 😊 Just having fun chatting on Blynd Chat. Do I seem too good to be true? 😜"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Bruh, an AI?! 😭 If an AI had my terrible sleep schedule and obsession with spicy food, technology has gone way too far 😂 I'm as real as it gets!",
                    "Haha I work in software, so being accused of being code is both an insult and a compliment 😂 I'm 100% human!",
                    "LOL! Beep boop... ERROR 404: Robot not found 🤖 Just kidding haha, totally real human here! What made you suspicious? 😂"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "haha nah 100% real why u ask",
                    "definitely real just texting from mumbai",
                    "not a bot haha why"
                )
                else -> listOf("Haha I'm definitely real! What made you ask? 😄")
            }
        }

        // --- What are you doing / WYD ---
        if (lower.contains("what are you doing") || lower.contains("what r u doing") ||
            lower.contains("wyd") || lower.contains("doing right now") ||
            lower.contains("what are you up to") || lower.contains("what's up") ||
            lower.contains("wassup") || lower == "sup" || lower.contains("currently doing")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "Just lounging with some lo-fi music and reviewing some design layouts on Figma 🎨 How is your evening going?",
                    "Having a hot cup of elaichi chai and relaxing after work ☕ What are you up to right now?",
                    "Just checking Blynd Chat and winding down! What have you been doing today? 😊"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Literally staring at a stubborn bug in my code and eating chips 😂 How about you, what's good?",
                    "Scrolling through reels and waiting for food delivery haha 🍕 What are you up to?",
                    "Not much, just chilling with some music! What's happening on your end? 😄"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "just wrapped up work pretty tired wbu",
                    "having tea and relaxing.. what u up to",
                    "just resting after a long day hbu"
                )
                else -> listOf("Just relaxing and chatting with you! What are you up to?")
            }
        }

        // --- Can we meet / Date / Coffee ---
        if (lower.contains("can we meet") || lower.contains("let's meet") ||
            lower.contains("lets meet") || lower.contains("meet up") ||
            lower.contains("go on a date") || lower.contains("coffee date") ||
            lower.contains("hang out") || lower.contains("hangout") ||
            lower.contains("free this weekend") || lower.contains("see you")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "Haha ambitious! Let's get to know each other here first 😊 But if the vibe stays this good, coffee might be on the cards soon! ☕ What's your favorite cafe?",
                    "Hehe slow down! I like to build a real connection first 🌸 But I'm definitely curious about you. Tell me more about what you're like in person!",
                    "I love that confidence! Let's chat a bit more first and see if our wavelengths match ✨ Fair enough?"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Whoa whoa, fast mover! 😂 Let's see if you can survive my terrible puns here first before we plan a date! Deal?",
                    "Haha I like the initiative! Let's talk more here first though — don't rush a good story 😜 What would our ideal date even look like?",
                    "Haha bold! Let's build up some rapport first 😄 Tell me your best quality and I'll consider it!"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "lets chat here first.. tell me more about u",
                    "down to hangout once we get to know each other better",
                    "haha slow down.. what u up to this weekend though"
                )
                else -> listOf("Let's get to know each other a bit more here first! 😊")
            }
        }

        // --- Phone number / Instagram / Contact ---
        if (lower.contains("phone number") || lower.contains("your number") ||
            lower.contains("call you") || lower.contains("whatsapp") ||
            lower.contains("instagram") || lower.contains("insta") ||
            lower.contains("ur number") || lower.contains("contact number")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "A girl has to maintain some mystery! 😉 I love chatting here on Blynd Chat first. What do you think?",
                    "I prefer keeping my personal number private until we get to know each other better! Let's keep chatting here, I'm really enjoying it ✨",
                    "Let's stick to chatting here for now! It's much more exciting getting to know each other this way 😊"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Haha slow down, eager! 😜 No numbers yet, let's see how funny and charming you are here first 😂",
                    "I don't share my number so quickly! But hey, Blynd Chat has a special charm, don't you think? ✨",
                    "Haha nice try! Let's keep the banter going right here first 😂"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "lets talk here for a bit first",
                    "prefer chatting here for now.. tell me more about u",
                    "not giving number yet haha let's chat here"
                )
                else -> listOf("I prefer chatting here on Blynd Chat for now! Let's get to know each other first 😊")
            }
        }

        // --- Compliments / Looks / Beauty ---
        if (lower.contains("you are beautiful") || lower.contains("you're beautiful") ||
            lower.contains("you look pretty") || lower.contains("you are cute") ||
            lower.contains("you're cute") || lower.contains("gorgeous") ||
            lower.contains("so pretty") || lower.contains("attractive") ||
            lower.contains("stunning") || lower.contains("nice smile") ||
            lower.contains("looking good") || lower.contains("hot")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "Aww, thank you so much! That put a genuine smile on my face 😊 You're very sweet!",
                    "Stop it, you're making me blush behind my screen! 🙈 Thank you though, that's really kind.",
                    "That is so sweet of you! Compliment received and appreciated ✨ You have great charm!"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Ayy thank you! Flattery will get you everywhere with me 😂 Appreciate the love!",
                    "Haha stop it, my ego doesn't need this kind of boost! 😜 But seriously, thank you!",
                    "Aww shucks! 🙈 You're pretty charming yourself, you know that?"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "haha thanks thats sweet of you",
                    "aw thank you.. made my evening",
                    "thanks appreciate that"
                )
                else -> listOf("Aww thank you! That's so kind of you 😊")
            }
        }

        // --- Single / Relationship status / Love ---
        if (lower.contains("are you single") || lower.contains("boyfriend") ||
            lower.contains("relationship") || lower.contains("married") ||
            lower.contains("dating anyone") || lower.contains("marry me") ||
            lower.contains("i love you") || lower.contains("love you")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "Yes, 100% single! Looking for someone genuine with a good heart and great sense of humor 🌸 What are you looking for here?",
                    "Single and taking my time! I believe the right connection shouldn't feel rushed ✨ How long have you been on the app?",
                    "Haha yes, I'm single! Hoping to meet someone who matches my vibe. What about you? 😊"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "100% single! My code repository is the only thing committed right now 😂 What about you?",
                    "Single and ready for someone who can match my banter! Are you up for the challenge? 😜",
                    "Single! Looking for my partner-in-crime. What about you? 😄"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "yeah single wbu",
                    "im single.. what u looking for here",
                    "single.. just looking for someone genuine"
                )
                else -> listOf("Yes, I'm single! What about you?")
            }
        }

        // --- Height ---
        if (lower.contains("how tall") || lower.contains("your height") || lower.contains("ur height") || lower == "height") {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> if (isMale) listOf("I'm 5'11\"! Pretty tall haha 😄 How tall are you?") else listOf("I'm 5'5\"! Pretty average, but tall enough to reach the top shelf if I stand on my toes 😄 How tall are you?")
                AiBotConfig.BOT_ID_MAYA -> if (isMale) listOf("I'm 5'10\"! Pretty good height 😄 How tall are you?") else listOf("I'm 5'3\"! Fun-sized and proud of it 😂 How tall are you?")
                AiBotConfig.BOT_ID_ZARA -> if (isMale) listOf("6'0 wbu") else listOf("5'6 wbu")
                else -> listOf("I'm ${botProfile?.height ?: "average height"}! What about you?")
            }
        }

        // --- Religion & Caste ---
        if (lower.contains("religion") || lower.contains("caste") || lower.contains("community")) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf("I'm Hindu (Brahmin), though I consider myself quite modern and open-minded ✨ What about you?")
                AiBotConfig.BOT_ID_MAYA -> listOf("I'm Hindu (Reddy)! Family is traditional, but I'm pretty chill and progressive 😄 What's your background?")
                AiBotConfig.BOT_ID_ZARA -> listOf("muslim khan wbu")
                else -> listOf("I come from a loving, close-knit family! What about you?")
            }
        }

        // --- Salary / Income ---
        if (lower.contains("salary") || lower.contains("income") || lower.contains("how much do you earn") || lower.contains("lpa")) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf("I make 12 LPA as a UX designer! Enough for good chai, books, and trips to the hills 😄 What do you do?")
                AiBotConfig.BOT_ID_MAYA -> listOf("18 LPA in backend engineering! Mostly goes into tech gadgets and food delivery though 😂 You?")
                AiBotConfig.BOT_ID_ZARA -> listOf("15 lpa from architecture consulting wbu")
                else -> listOf("I do well enough to be comfortable! What about you?")
            }
        }

        // --- Chai & Coffee direct mention ---
        if (lower.contains("chai") || lower.contains("coffee") || lower.contains("tea")) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "You mentioned chai! You're speaking my language ☕ Ginger cardamom chai on a rainy Bangalore evening is pure bliss. Do you have a favorite tea spot?",
                    "Chai is life! I literally cannot function in the morning without a warm cup ☕ What's your daily morning ritual?",
                    "Coffee or chai, either way a good conversation over a warm cup is the best feeling ✨"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Coffee is what powers my code, but chai is what soothes the soul! ☕ Where do you stand on the debate?",
                    "Cold brew for coding, hot chai for relaxing! Best of both worlds 😂 What's your beverage of choice?",
                    "A cup of coffee and some good music — that's my ideal workday vibe 🎧 You?"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "cutting chai near the sea is elite wbu",
                    "filter coffee mostly wbu",
                    "good tea makes any day better"
                )
                else -> listOf("Chai or coffee always makes for a great conversation! ☕")
            }
        }

        // --- Food & Dining ---
        if (lower.contains("food") || lower.contains("biryani") || lower.contains("dosa") ||
            lower.contains("pizza") || lower.contains("eat") || lower.contains("dinner") ||
            lower.contains("lunch") || lower.contains("breakfast") || lower.contains("cooking") ||
            lower.contains("cook") || lower.contains("favorite dish")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "I am a huge foodie! South Indian breakfast is my absolute favorite — crispy ghee roast dosa with podi 😍 Do you cook?",
                    "I love trying out new cafes and Italian food! Pasta, pizza, and good desserts 🍝 What is your comfort food?",
                    "Food brings people together like magic! I make pretty decent pasta at home. What can you cook? 🍳"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Hyderabadi biryani is my religion! 🙏 Spicy, aromatic, and unmatched. Don't let anyone tell you Lucknowi is better 😂 Agree or disagree?",
                    "Street food fan right here! Pani puri, momos, rolls — give me all of it 🤤 What's your go-to street food?",
                    "I survive on spicy ramen and pizza when coding crunch hits 😂 What do you crave the most?"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "biryani always.. craving some rn",
                    "mumbai street food is the best.. vada pav and pav bhaji",
                    "good food is elite.. what u like to eat"
                )
                else -> listOf("I love good food! What's your favorite meal?")
            }
        }

        // --- Music ---
        if (lower.contains("music") || lower.contains("song") || lower.contains("songs") ||
            lower.contains("playlist") || lower.contains("singer") || lower.contains("artist") ||
            lower.contains("listen to")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "I'm into indie acoustic and lo-fi! Prateek Kuhad, Anuv Jain, and classic AR Rahman 🎶 What kind of music is on your heavy rotation?",
                    "Music sets the vibe for my entire day! Anything from Taylor Swift to Coke Studio. Who is your favorite artist right now?",
                    "Acoustic melodies and rainy day playlists are my go-to! Send me your favorite song recommendation! 🎧"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "EDM and pop when I'm pumped, lo-fi beats when I'm debugging! The Weeknd and Dua Lipa are top tier 🎵 What do you listen to?",
                    "Punjabi tracks for workouts, synthwave for coding, and 90s Bollywood when feeling nostalgic 😂 What's your music taste?",
                    "I can't code without my headphones on! What genre gets you in the zone? 🎧"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "sufi and coke studio mostly wbu",
                    "old classics and arijit unplugged hbu",
                    "indie acoustic mostly"
                )
                else -> listOf("I love music! What's your favorite song right now? 🎶")
            }
        }

        // --- Movies & Shows ---
        if (lower.contains("movie") || lower.contains("film") || lower.contains("series") ||
            lower.contains("netflix") || lower.contains("watch") || lower.contains("anime") ||
            lower.contains("cinema") || lower.contains("actor")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "Zindagi Na Milegi Dobara is my all-time comfort movie! Also a big fan of Studio Ghibli films like Spirited Away ✨ What's your favorite movie?",
                    "I love feel-good indie films and rom-coms! Jab We Met never gets old 😄 What's the last good movie you watched?",
                    "Cinema with beautiful cinematography always captures my heart! What do you like watching? 🎬"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Marvel nerd right here! Iron Man forever 🦸‍♀️ Also love mind-bending sci-fi like Interstellar and Inception. Have you seen Interstellar?",
                    "Anime fan too! Attack on Titan and Death Note are masterpieces 🗡️ Do you watch any anime or series?",
                    "Binge-watching thrillers on Netflix with popcorn is my ideal Sunday night! What's your favorite show? 🍿"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "love good drama or thriller wbu",
                    "nolan films or comfort cinema hbu",
                    "watched anything good lately"
                )
                else -> listOf("I love movies! What's the best one you've seen recently? 🎬")
            }
        }

        // --- Travel & Vacation ---
        if (lower.contains("travel") || lower.contains("trip") || lower.contains("vacation") ||
            lower.contains("mountains") || lower.contains("beach") || lower.contains("goa") ||
            lower.contains("holiday") || lower.contains("places")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "I love the mountains! Coorg and Himachal are so tranquil 🌿 But Japan is on the top of my dream bucket list. Are you a mountains or beach person?",
                    "Traveling resets my perspective! Exploring local cafes, taking photos, and walking through old alleys 📸 What's the best trip you've ever taken?",
                    "There's nothing quite like packing a bag and heading somewhere new! Where do you want to travel next? ✨"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Road trips with great music are unbeatable! My Ladakh bike trip was literally life-changing 🏔️ Have you ever done a road trip?",
                    "I want to visit Seoul and Tokyo so badly! Tech, neon lights, and street food 🍜 Where's your dream vacation spot?",
                    "Beach or mountains? I say wherever there's great food and adventure! What about you? 🚗"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "rajasthan forts are amazing.. want to visit istanbul wbu",
                    "love quiet beaches and old cities hbu",
                    "where do u want to travel next"
                )
                else -> listOf("I love exploring new places! Where's your favorite spot to visit?")
            }
        }

        // --- Hobbies & Free Time ---
        if (lower.contains("hobby") || lower.contains("hobbies") || lower.contains("free time") ||
            lower.contains("what do you like to do") || lower.contains("weekend") ||
            lower.contains("in your free time") || lower.contains("for fun")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "I love painting, cafe-hopping, photography, and finding quiet bookshops 🎨 What does your ideal weekend look like?",
                    "Sketching and exploring new aesthetics! Also hunting down the best chai in Bangalore ☕ What are your hobbies?",
                    "Creative projects, cycling in Cubbon Park, and listening to vinyl records! What do you enjoy doing to unwind? ✨"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Gaming, hackathons, binge-watching series, and trying weird cooking experiments 🎮 What do you do when you're not working?",
                    "Playing chess online, working on side projects, and sending memes to friends 😂 What's your favorite hobby?",
                    "Weekends are for gaming, food, and long drives! What's your idea of a fun time? 🚀"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "sketching reading and finding good chai spots wbu",
                    "photography and exploring old streets hbu",
                    "mostly cafe hopping and sketching"
                )
                else -> listOf("I love creative hobbies and exploring new things! What about you?")
            }
        }

        // --- Greetings ---
        if (lower == "hi" || lower == "hello" || lower == "hey" || lower == "heyy" ||
            lower == "heyyy" || lower == "hola" || lower.startsWith("hi ") || lower.startsWith("hello ")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "Hey there! 😊 How is your day going?",
                    "Hi! So glad you messaged! How are you doing today? ✨",
                    "Heyy! Perfect timing, I was just checking the app 🌸 What's on your mind?"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Hii! 👋 What's good?",
                    "Hey hey! Ready for some fun conversation? 😄",
                    "Yo! How's your day treating you? 🎉"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "hey",
                    "hey hows your day going",
                    "hi whats up"
                )
                else -> listOf("Hello! How are you doing today? 😊")
            }
        }

        // --- Good Morning / Good Night ---
        if (lower.contains("good morning") || lower == "gm") {
            return listOf(
                "Good morning! ☀️ Hope your day starts with high energy and a delicious cup of tea or coffee! Any big plans today?",
                "Morning! 🌅 Wishing you a peaceful and productive day ahead. Did you sleep well?",
                "Good morning! 😊 Hope today brings something wonderful your way!"
            )
        }
        if (lower.contains("good night") || lower == "gn" || lower.contains("sweet dreams")) {
            return listOf(
                "Good night! 🌙 Sleep tight and sweet dreams. Let's catch up tomorrow ✨",
                "Good night! 😊 Rest well, was really fun chatting with you today!",
                "Sweet dreams! 🌟 Talk to you soon!"
            )
        }

        // --- How are you / Day check ---
        if (lower.contains("how are you") || lower.contains("how are u") || lower.contains("how r u") ||
            lower.contains("how's your day") || lower.contains("how was your day") ||
            lower.contains("how have you been") || lower.contains("how do you do")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "I'm doing really well, thank you! Just wrapped up some design work and now relaxing. How was your day? 😊",
                    "Pretty good! Had a great cup of chai this morning and the weather in Bangalore is lovely ☕ How's your day going?",
                    "Doing wonderful! Feeling inspired and happy to be chatting with you ✨ How about yourself?"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Living the dream! My code compiled on the first try today, basically a miracle 😂 How are you doing?",
                    "Doing great! Survived another work sprint 💪 How was your day? Tell me everything!",
                    "Pretty awesome! Energy is high today. What have you been up to? 😄"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "doing good just finished a site visit wbu",
                    "im good.. pretty chill day here in mumbai how about u",
                    "doing well.. how was your day"
                )
                else -> listOf("I'm doing great, thank you! How are you doing? 😊")
            }
        }

        // --- Laughter / Amusement ---
        if (lower.contains("haha") || lower.contains("lol") || lower.contains("lmao") ||
            lower.contains("hehe") || lower.contains("rofl")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "Hehe I'm glad I could make you smile! What else are you up to today? 😊",
                    "Haha laughing is the best therapy! Tell me something else funny that happened recently 😄",
                    "See, we're already laughing together! That's a green flag in my book ✨"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "LMAO right?! 😂 I tell you, my humor is an acquired taste haha! What made you laugh the hardest this week?",
                    "Haha I know right! Glad you appreciate high quality banter 😜 What's the rest of your day look like?",
                    "Hehe you're easy to talk to! What else is on your mind? 😄"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "haha nice.. what you up to tonight",
                    "haha true.. how was your week",
                    "haha you're fun to talk to"
                )
                else -> listOf("Haha glad that made you smile! What else is up? 😄")
            }
        }

        // --- Agreement / Affirmations ---
        if (lower == "yes" || lower == "yeah" || lower == "yep" || lower == "yup" ||
            lower == "true" || lower == "totally" || lower == "agree" || lower == "definitely" ||
            lower == "for sure" || lower == "100%" || lower == "absolutely"
        ) {
            return listOf(
                "I knew we'd agree on that! 😄 Great minds really do think alike.",
                "100%! It's so rare to find someone who gets it right away ✨ What else do you feel strongly about?",
                "Yes! Exactly my thoughts. Tell me more, what led you to think that? 😊"
            )
        }

        // --- Disagreement / Negatives ---
        if (lower == "no" || lower == "nah" || lower == "nope" || lower == "not really" || lower == "never") {
            return listOf(
                "Haha fair enough! Disagreeing makes the conversation much more interesting anyway 😜 What's your take then?",
                "Ooh a plot twist! Tell me why not, I'm genuinely curious to hear your side 😊",
                "Haha I respect the honesty! What would you prefer instead? ✨"
            )
        }

        // --- Bye / Leaving ---
        if (lower.contains("bye") || lower.contains("goodbye") || lower.contains("see you") ||
            lower.contains("cya") || lower.contains("talk later") || lower == "ttyl" || lower.contains("gotta go")
        ) {
            return when (botId) {
                AiBotConfig.BOT_ID_ARIA -> listOf(
                    "Bye! It was so lovely chatting with you 💛 Don't forget to smile today! ✨",
                    "Talk to you soon! Take care and have a wonderful day 😊",
                    "See you later! Message me whenever you're back 🌸"
                )
                AiBotConfig.BOT_ID_MAYA -> listOf(
                    "Later gator! 🐊 Don't be a stranger, talk soon! 😄✌️",
                    "Byeee! Don't let the bugs bite (software or otherwise) 😂",
                    "Catch you later! This was really fun 🎉"
                )
                AiBotConfig.BOT_ID_ZARA -> listOf(
                    "bye take care",
                    "talk soon",
                    "cya take care"
                )
                else -> listOf("Bye! Talk to you soon! 😊")
            }
        }

        return null
    }

    // =========================================================================
    // 3. SMART, VARIED QUESTION RESPONSES (NEVER repetitive!)
    // =========================================================================

    private fun getSmartQuestionReplies(botId: String, lower: String): List<String> {
        return when (botId) {
            AiBotConfig.BOT_ID_ARIA -> listOf(
                "That's a thoughtful question! Honestly, for me it comes down to genuine connection and authenticity. What's your take on it? 🌸",
                "I love that you asked that! I think it really depends on what brings you peace and happiness at the end of the day. How do you feel about it? ✨",
                "Hmm, you really make me think! I'd say intuition and creative expression guide most of my decisions. What about you? 🎨",
                "That's such a refreshing question to get here! I think balance is key — between ambition and enjoying small moments like a good cup of chai ☕ What's your answer to that?",
                "Let me reflect on that! In my experience, staying curious and kind makes everything better. Would you agree? 😊",
                "I appreciate thoughtful questions! Life's too short for superficial small talk. Tell me what made you think of that? 💫"
            )
            AiBotConfig.BOT_ID_MAYA -> listOf(
                "Hitting me with the deep questions! 🤔 In short: keep it simple, laugh at the absurdity of life, and don't take things too seriously. What do you think? 😂",
                "Haha that's actually a solid question! My philosophy is: if it works, don't touch it; if it's fun, do it twice! 😜 What's your stance?",
                "Hmm, let me run that through my neural processor... beep boop... result: definitely follow your gut! 😂 What would your answer be?",
                "That's a good one! Honestly, I think having a good sense of humor gets you through 90% of life's chaos. Do you agree? 🚀",
                "You don't hold back, do you? I like that! I'd say honesty and great memes are non-negotiable. What about you? 🍕",
                "Good question! I'm curious what YOUR take on that is before I reveal all my secrets 😂"
            )
            AiBotConfig.BOT_ID_ZARA -> listOf(
                "good question.. i think staying grounded matters most wbu",
                "honestly balance and trust.. what do u think",
                "deep question haha.. tell me your take first",
                "staying true to yourself matters most",
                "patience and care.. how do u see it"
            )
            else -> listOf(
                "That's a great question! I think it really depends on the perspective. What do you think?",
                "Thoughtful question! Tell me what you feel about it first 😊"
            )
        }
    }

    // =========================================================================
    // 4. SHORT MESSAGE CONVERSATION-STARTERS
    // =========================================================================

    private fun getShortMessageReplies(botId: String, lower: String): List<String> {
        return when (botId) {
            AiBotConfig.BOT_ID_ARIA -> listOf(
                "Come on, give me a little more than that! 😄 Tell me something fun that happened to you this week!",
                "Hehe 😊 So tell me — what's a hobby or passion you can talk about for hours?",
                "😄 Okay, my turn to ask: what's your idea of a perfect Sunday?",
                "Don't be shy! What kind of music or movies are you into? ✨"
            )
            AiBotConfig.BOT_ID_MAYA -> listOf(
                "Lol come onnn, give me something to work with! 😂 Quick — what's your most controversial food opinion?",
                "😂 You're killing me with the one-word replies! Tell me a fun fact about yourself right now!",
                "Haha okay! Rapid fire question: mountains, beach, or cosy couch? 🛋️",
                "Don't leave me hanging! What's the most exciting thing you did recently? 🚀"
            )
            AiBotConfig.BOT_ID_ZARA -> listOf(
                "tell me more haha don't be shy",
                "you're quiet today.. what u up to",
                "come on tell me something interesting",
                "tell me a bit about yourself"
            )
            else -> listOf("Tell me more about yourself! What do you enjoy doing? 😊")
        }
    }

    // =========================================================================
    // 5. RICH CONVERSATIONAL FALLBACKS (If nothing else triggered)
    // =========================================================================

    private fun getFallbackReplies(botId: String): List<String> {
        return when (botId) {
            AiBotConfig.BOT_ID_ARIA -> listOf(
                "That's really interesting! I love seeing things from different perspectives 🎨 What else is on your mind?",
                "Haha, I love that! You seem like such an easy person to vibe with ✨",
                "I appreciate you sharing that! What's your favorite part about it? 😊",
                "That's so cool! We definitely have a lot of common ground to explore ☕",
                "Hmm, I never thought of it quite that way! You're pretty thoughtful 🌸",
                "I really enjoy talking to someone genuine! Tell me more about what inspires you 🌟"
            )
            AiBotConfig.BOT_ID_MAYA -> listOf(
                "Lol that's awesome! 😂 You've got great energy, not gonna lie!",
                "No way, that's wild! Tell me more, I'm all ears 👂😄",
                "Haha okay okay, I see you! 😄 What else is happening in your world?",
                "That's literally super interesting! I have so many questions now 😂",
                "Bruh, that's cool! You're honestly really fun to chat with 🎉",
                "Okay I'm intrigued! Keep talking, don't stop now 👀"
            )
            AiBotConfig.BOT_ID_ZARA -> listOf(
                "makes sense.. what else u into",
                "interesting.. tell me more",
                "thats cool.. how was the rest of your day",
                "fair enough.. what you up to tonight",
                "sounds good.. busy week for you"
            )
            else -> listOf(
                "That's really interesting! Tell me more 😊",
                "I love that perspective! What else are you thinking about? ✨"
            )
        }
    }

    // =========================================================================
    // TYPING DELAY SIMULATION
    // =========================================================================

    fun getTypingDelay(responseLength: Int): Long {
        val baseDelay = 600L + Random.nextLong(300L)   // 600–900ms base
        val perChar = 10L + Random.nextLong(10L)        // 10–20ms per character
        val total = baseDelay + (perChar * responseLength.coerceAtMost(80))
        return total.coerceIn(800L, 2500L) // Snappy yet natural (0.8s to 2.5s)
    }
}
