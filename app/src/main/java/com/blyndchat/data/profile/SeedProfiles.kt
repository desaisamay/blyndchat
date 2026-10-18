package com.blyndchat.data.profile

import com.blyndchat.data.model.Profile

/**
 * 60 authentic Indian profiles for each gender with diverse names, professions,
 * cities, engaging bios, and chat preferences.
 */
object SeedProfiles {

    private val AVATAR_FILES = listOf(
        "Avatar_2.png", "Avatar_21.png", "Avatar_22.png", "Avatar_23.png",
        "Avatar_3.png", "Avatar_31.png", "Avatar_32.png", "Avatar_34.png",
        "Avatar_4.png", "Avatar_41.png", "Avatar_42.png", "Avatar_43.png",
        "Avatar_5.png", "Avatar_51.png", "Avatar_52.png", "Avatar_53.png",
        "Avatar_6.png", "Avatar_61.png", "Avatar_62.png", "Avatar_63.png",
        "Avatar_7.png", "Avatar_71.png", "Avatar_72.png", "Avatar_73.png",
        "Avatar_8.png", "Avatar_81.png", "Avatar_82.png", "Avatar_83.png"
    )

    private fun avatar(index: Int): String =
        "file:///android_asset/avatars/${AVATAR_FILES[index % AVATAR_FILES.size]}"

    val FEMALE_PROFILES: List<Profile> = listOf(
        Profile(
            id = "seed_f_01", name = "Ananya Sharma", age = 25, height = "5'4\"",
            profession = "Product / UX Designer", location = "Bangalore", imageUrl = avatar(0),
            about = "Chai enthusiast and typography nerd. Exploring hidden cafes in Indiranagar on weekends ☕🎨",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 30",
            preferredTopics = "Art & Design, Music, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_f_02", name = "Diya Patel", age = 24, height = "5'3\"",
            profession = "Software Engineer", location = "Pune", imageUrl = avatar(1),
            about = "Writing clean Kotlin code by day, gaming by night. Let's debate iOS vs Android or favorite anime 🎮💻",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Tech & AI, Gaming, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office Drama, Salary"
        ),
        Profile(
            id = "seed_f_03", name = "Sneha Iyer", age = 27, height = "5'5\"",
            profession = "Doctor / Healthcare", location = "Chennai", imageUrl = avatar(2),
            about = "Doctor with a passion for classical Carnatic tunes and filter coffee. Spontaneous weekend road trips! 🩺☕",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "26 - 32",
            preferredTopics = "Science, Music, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats, Cynicism"
        ),
        Profile(
            id = "seed_f_04", name = "Pooja Verma", age = 26, height = "5'6\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = avatar(3),
            about = "Brand strategist who speaks fluent sarcasm. Foodie on a quest for the best momos in Delhi 🥟✨",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "24 - 30",
            preferredTopics = "Food & Cooking, Travel, Psychology", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships, Religion"
        ),
        Profile(
            id = "seed_f_05", name = "Riya Sen", age = 23, height = "5'2\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = avatar(4),
            about = "Poetry, College Street book browsing, and endless cups of sweet chai. Hopeless romantic 📚☕",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "22 - 28",
            preferredTopics = "Books, Philosophy, Art & Design", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Money"
        ),
        Profile(
            id = "seed_f_06", name = "Tanvi Deshmukh", age = 28, height = "5'7\"",
            profession = "Architect", location = "Mumbai", imageUrl = avatar(5),
            about = "Restoring heritage spaces and chasing coastal breezes at Marine Drive. Big believer in mindful living 🏛️🌊",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "26 - 33",
            preferredTopics = "Architecture, Travel, Fitness", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics, Negativity"
        ),
        Profile(
            id = "seed_f_07", name = "Ishita Roy", age = 25, height = "5'4\"",
            profession = "Finance / Banking", location = "Mumbai", imageUrl = avatar(6),
            about = "Crunching spreadsheets on weekdays, indie rock gigs on Saturdays. Always up for a good laugh 🎸📊",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "24 - 30",
            preferredTopics = "Music, Movies & TV, Travel", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Salary, Drama"
        ),
        Profile(
            id = "seed_f_08", name = "Meera Nair", age = 26, height = "5'5\"",
            profession = "Teacher / Professor", location = "Kochi", imageUrl = avatar(7),
            about = "Literature lecturer by trade, nature photographer by heart. Monsoons, coconut lagoons, and jazz 🌿📷",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Science, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Office Drama"
        ),
        Profile(
            id = "seed_f_09", name = "Shreya Joshi", age = 24, height = "5'3\"",
            profession = "Student", location = "Pune", imageUrl = avatar(8),
            about = "Masters in Psychology. Looking for late-night intellectual debates about human behavior and universe 🌌🧠",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "22 - 27",
            preferredTopics = "Psychology, Philosophy, Science", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Ex-partners, Small talk"
        ),
        Profile(
            id = "seed_f_10", name = "Neha Gupta", age = 29, height = "5'6\"",
            profession = "Entrepreneur / Business", location = "Jaipur", imageUrl = avatar(9),
            about = "Building a sustainable fashion label. Loving Rajasthani heritage, royal palaces, and good coffee ☕👗",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "27 - 35",
            preferredTopics = "Art & Design, Travel, Tech & AI", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_f_11", name = "Kavya Reddy", age = 25, height = "5'5\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = avatar(10),
            about = "Frontend developer obsessed with buttery-smooth micro-animations. Biryani connoisseur 🍛💻",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Food & Cooking, Gaming", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office Drama"
        ),
        Profile(
            id = "seed_f_12", name = "Aditi Rao", age = 26, height = "5'4\"",
            profession = "Lawyer / Legal", location = "Bangalore", imageUrl = avatar(11),
            about = "Corporate lawyer with a soft spot for indie dogs, pottery, and podcasts. Let's share book recommendations 🐕📖",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Books, Travel, Psychology", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Court stories, Politics"
        ),
        Profile(
            id = "seed_f_13", name = "Simran Kaur", age = 24, height = "5'7\"",
            profession = "Fitness / Wellness", location = "Chandigarh", imageUrl = avatar(12),
            about = "Certified yoga trainer and marathoner. Loving sunrises, protein smoothies, and Punjabi humor 🏃‍♀️☀️",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Fitness, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Salary, Drama"
        ),
        Profile(
            id = "seed_f_14", name = "Priyanka Mehta", age = 28, height = "5'5\"",
            profession = "Product / UX Designer", location = "Ahmedabad", imageUrl = avatar(13),
            about = "Simplifying complex workflows. Weekend hobbyist baker of sourdough and cinnamon rolls 🥖🎨",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "26 - 32",
            preferredTopics = "Art & Design, Food & Cooking, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial banter"
        ),
        Profile(
            id = "seed_f_15", name = "Anika Malhotra", age = 23, height = "5'4\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = avatar(14),
            about = "Creative copywriter. Believes listening to music with headphones is the closest thing to teleportation 🎧✨",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "21 - 27",
            preferredTopics = "Music, Movies & TV, Art & Design", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Religion"
        ),
        Profile(
            id = "seed_f_16", name = "Nidhi Agarwal", age = 27, height = "5'3\"",
            profession = "Finance / Banking", location = "Kolkata", imageUrl = avatar(15),
            about = "Fintech analyst who finds peace in classical piano and evening riverside strolls 🎹🌊",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Music, Travel, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Past Relationships"
        ),
        Profile(
            id = "seed_f_17", name = "Kriti Saxena", age = 25, height = "5'6\"",
            profession = "Software Engineer", location = "Bangalore", imageUrl = avatar(16),
            about = "AI researcher fascinated by large language models and cognitive science. Stargazing enthusiast 🔭🤖",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "24 - 30",
            preferredTopics = "Tech & AI, Science, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Office Drama"
        ),
        Profile(
            id = "seed_f_18", name = "Shruti Menon", age = 26, height = "5'5\"",
            profession = "Architect", location = "Goa", imageUrl = avatar(17),
            about = "Living by the sea, designing eco-resorts. Slow mornings, watercolor painting, and fresh seafood 🏖️🎨",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Architecture, Travel, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "City hustle complaints"
        ),
        Profile(
            id = "seed_f_19", name = "Pallavi Kulkarni", age = 29, height = "5'4\"",
            profession = "Doctor / Healthcare", location = "Mumbai", imageUrl = avatar(18),
            about = "Pediatrician who loves children's laughter, trekking the Sahyadris, and spicy street food ⛰️🩺",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "27 - 34",
            preferredTopics = "Fitness, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital stress"
        ),
        Profile(
            id = "seed_f_20", name = "Aayushi Jain", age = 24, height = "5'2\"",
            profession = "Student", location = "Jaipur", imageUrl = avatar(19),
            about = "Studying modern history. Passionate about art galleries, old monuments, and street photography 🏛️📸",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Books, Art & Design, Travel", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exam stress, Money"
        ),
        Profile(
            id = "seed_f_21", name = "Radhika Pillai", age = 26, height = "5'5\"",
            profession = "Writer / Content Creator", location = "Bangalore", imageUrl = avatar(20),
            about = "Writing tech essays and short stories. Collecting vintage postcards and brewing artisanal pour-overs ☕📝",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Tech & AI, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Office Drama"
        ),
        Profile(
            id = "seed_f_22", name = "Swati Mishra", age = 25, height = "5'4\"",
            profession = "Marketing / Growth", location = "Lucknow", imageUrl = avatar(21),
            about = "Digital marketer with a poetic soul. Food lover who believes kebabs cure any bad mood 🍢✨",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Food & Cooking, Movies & TV, Music", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Past Relationships"
        ),
        Profile(
            id = "seed_f_23", name = "Roshni Das", age = 27, height = "5'6\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = avatar(22),
            about = "Backend engineer who loves high-throughput distributed systems and late-night highway drives 🚗⚡",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Gaming, Travel", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work complaints, Salary"
        ),
        Profile(
            id = "seed_f_24", name = "Natasha Bose", age = 24, height = "5'5\"",
            profession = "Product / UX Designer", location = "Kolkata", imageUrl = avatar(23),
            about = "Designing mobile apps with empathy. Big fan of retro synthwave music and cinema classics 🎬🎶",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Movies & TV, Music, Art & Design", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial talk"
        ),
        Profile(
            id = "seed_f_25", name = "Preeti Singhania", age = 28, height = "5'3\"",
            profession = "Entrepreneur / Business", location = "Delhi NCR", imageUrl = avatar(24),
            about = "Founder of an organic skincare brand. Loves slow travel, weekend farmers markets, and podcasts 🌿🎧",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "26 - 34",
            preferredTopics = "Science, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_f_26", name = "Shalini Bhatt", age = 25, height = "5'7\"",
            profession = "Teacher / Professor", location = "Dehradun", imageUrl = avatar(25),
            about = "High school English teacher surrounded by mountain misty air. Mountain lover and weekend trekker 🏔️🌲",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 30",
            preferredTopics = "Books, Travel, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "City traffic, Cynicism"
        ),
        Profile(
            id = "seed_f_27", name = "Divya Hegde", age = 26, height = "5'4\"",
            profession = "Finance / Banking", location = "Bangalore", imageUrl = avatar(26),
            about = "Investment banker who de-stresses with badminton and acoustic guitar sessions 🏸🎸",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "24 - 30",
            preferredTopics = "Music, Fitness, Movies & TV", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Stock tips, Work stress"
        ),
        Profile(
            id = "seed_f_28", name = "Rhea Kapoor", age = 23, height = "5'5\"",
            profession = "Student", location = "Mumbai", imageUrl = avatar(27),
            about = "Studying Media and Communication. Obsessed with indie documentaries, pop culture, and bubble tea 🧋🎥",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "21 - 26",
            preferredTopics = "Movies & TV, Music, Art & Design", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Academic pressure"
        ),
        Profile(
            id = "seed_f_29", name = "Sanya Chawla", age = 27, height = "5'6\"",
            profession = "Lawyer / Legal", location = "Chandigarh", imageUrl = avatar(0),
            about = "Advocate who believes in empathy and fairness. Weekend badminton matches and spicy street food lover 🏸⚖️",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Books, Philosophy, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Past Relationships, Gossip"
        ),
        Profile(
            id = "seed_f_30", name = "Bhavna Trivedi", age = 25, height = "5'3\"",
            profession = "Software Engineer", location = "Ahmedabad", imageUrl = avatar(1),
            about = "Cloud architect building scalable microservices. Loves baking banana bread and playing chess 🍌♟️",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Food & Cooking", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics"
        ),
        Profile(
            id = "seed_f_31", name = "Tara Sundaram", age = 26, height = "5'5\"",
            profession = "Doctor / Healthcare", location = "Chennai", imageUrl = avatar(2),
            about = "Dermatologist with an artistic soul. Nature lover, watercolor painter, and avid podcast listener 🌿🎧",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 31",
            preferredTopics = "Science, Art & Design, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Work stress"
        ),
        Profile(
            id = "seed_f_32", name = "Trisha Mukherjee", age = 24, height = "5'4\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = avatar(3),
            about = "Cultural journalist documenting Kolkata's heritage sweets and indie music scene 🍮🎵",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Food & Cooking, Music, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics"
        ),
        Profile(
            id = "seed_f_33", name = "Alisha Varma", age = 28, height = "5'6\"",
            profession = "Architect", location = "Bangalore", imageUrl = avatar(4),
            about = "Passionate about biophilic green building design. Big fan of house plants, jazz, and filter coffee 🪴☕",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "26 - 33",
            preferredTopics = "Architecture, Music, Philosophy", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Gossip"
        ),
        Profile(
            id = "seed_f_34", name = "Payal Sethi", age = 25, height = "5'2\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = avatar(5),
            about = "Campaign strategist who loves witty ad copy, stand-up comedy nights, and weekend brunches 🎤🥞",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Movies & TV, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_f_35", name = "Karishma Chopra", age = 27, height = "5'7\"",
            profession = "Product / UX Designer", location = "Mumbai", imageUrl = avatar(6),
            about = "UX researcher curious about how people make everyday choices. Sunsets at Bandra Bandstand lover 🌅📱",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Psychology, Art & Design, Tech & AI", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics"
        ),
        Profile(
            id = "seed_f_36", name = "Avani Kothari", age = 24, height = "5'3\"",
            profession = "Finance / Banking", location = "Pune", imageUrl = avatar(7),
            about = "Equity analyst by day, Kathak dancer by passion. Believer in balancing discipline with pure joy 💃📊",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "23 - 29",
            preferredTopics = "Art & Design, Music, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Money brags"
        ),
        Profile(
            id = "seed_f_37", name = "Lavanya Nambiar", age = 26, height = "5'5\"",
            profession = "Teacher / Professor", location = "Kochi", imageUrl = avatar(8),
            about = "Teaching mathematics while reading mystery thrillers. Loves peaceful monsoon afternoons and spice teas 🌧️☕",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "24 - 30",
            preferredTopics = "Books, Science, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Drama, Gossip"
        ),
        Profile(
            id = "seed_f_38", name = "Sanjana Mathur", age = 25, height = "5'4\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = avatar(9),
            about = "Mobile engineer creating intuitive apps. Weekend baker, dog shelter volunteer, and trivia geek 🐕🧁",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Movies & TV", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work stress"
        ),
        Profile(
            id = "seed_f_39", name = "Parul Bhatia", age = 29, height = "5'6\"",
            profession = "Entrepreneur / Business", location = "Chandigarh", imageUrl = avatar(10),
            about = "Running a boutique organic cafe. Loves experimenting with fusion cuisine and meeting genuine souls 🥗☕",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "27 - 35",
            preferredTopics = "Food & Cooking, Travel, Books", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Religion"
        ),
        Profile(
            id = "seed_f_40", name = "Diksha Rawat", age = 23, height = "5'4\"",
            profession = "Student", location = "Dehradun", imageUrl = avatar(11),
            about = "Forestry and ecology student. Birdwatcher, camper, and lover of mountain campfires and guitar songs 🌲🔥",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "21 - 27",
            preferredTopics = "Science, Music, Travel", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "City gossip"
        ),
        Profile(
            id = "seed_f_41", name = "Sonali Dutta", age = 26, height = "5'5\"",
            profession = "Doctor / Healthcare", location = "Kolkata", imageUrl = avatar(12),
            about = "Surgeon in training. Passionate about health equity, playing table tennis, and listening to Rabindrasangeet 🏓🩺",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Science, Fitness, Philosophy", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Medical complaints"
        ),
        Profile(
            id = "seed_f_42", name = "Mansi Parekh", age = 27, height = "5'3\"",
            profession = "Lawyer / Legal", location = "Mumbai", imageUrl = avatar(13),
            about = "Intellectual property lawyer. Loves art exhibitions at Kala Ghoda, theatre plays, and strong Americanos ☕🎭",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Art & Design, Books, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Office Drama"
        ),
        Profile(
            id = "seed_f_43", name = "Urvashi Goswami", age = 25, height = "5'6\"",
            profession = "Writer / Content Creator", location = "Jaipur", imageUrl = avatar(14),
            about = "Travel essayist exploring remote village crafts. Loves golden sunsets and folk music sessions 🌅🎶",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "23 - 30",
            preferredTopics = "Travel, Art & Design, Books", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chatter"
        ),
        Profile(
            id = "seed_f_44", name = "Akanksha Pandey", age = 24, height = "5'4\"",
            profession = "Marketing / Growth", location = "Bangalore", imageUrl = avatar(15),
            about = "Community builder who brings tech enthusiasts together. Passionate about indie podcasts and board games 🎲🎙️",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Tech & AI, Gaming, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office politics"
        ),
        Profile(
            id = "seed_f_45", name = "Ishani Ghosh", age = 28, height = "5'5\"",
            profession = "Software Engineer", location = "Pune", imageUrl = avatar(16),
            about = "DevOps wizard who loves clean pipelines, spicy street pani puri, and weekend bicycling rides 🚲🍲",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "26 - 33",
            preferredTopics = "Tech & AI, Fitness, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Ex-partners"
        ),
        Profile(
            id = "seed_f_46", name = "Garima Grover", age = 26, height = "5'7\"",
            profession = "Product / UX Designer", location = "Delhi NCR", imageUrl = avatar(17),
            about = "Designing inclusive interfaces. Loves museum hopping, thrift store fashion, and iced matcha lattes 🍵🏛️",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Art & Design, Psychology, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_f_47", name = "Juhi Shukla", age = 25, height = "5'3\"",
            profession = "Finance / Banking", location = "Lucknow", imageUrl = avatar(18),
            about = "Wealth advisor with a sweet tooth for Malai Makkhan. Exploring heritage cuisine and old Awadhi ghazals 🍮🎶",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Food & Cooking, Music, Books", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Salary, Status"
        ),
        Profile(
            id = "seed_f_48", name = "Nikita Sood", age = 24, height = "5'5\"",
            profession = "Fitness / Wellness", location = "Chandigarh", imageUrl = avatar(19),
            about = "Pilates instructor and mindful lifestyle advocate. Sunrise hikes, acai bowls, and positive vibes only 🧘‍♀️☀️",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Fitness, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Drama, Cynicism"
        ),
        Profile(
            id = "seed_f_49", name = "Esha Somani", age = 27, height = "5'4\"",
            profession = "Architect", location = "Ahmedabad", imageUrl = avatar(20),
            about = "Urban planner redesigning public plazas. Enthusiast of brutalist architecture and clay pottery 🏺🏙️",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Architecture, Art & Design, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk"
        ),
        Profile(
            id = "seed_f_50", name = "Ruchi Agnihotri", age = 26, height = "5'6\"",
            profession = "Teacher / Professor", location = "Bhopal", imageUrl = avatar(21),
            about = "College chemistry lecturer who loves explaining everyday magic. Sunset lover by Upper Lake 🌅🧪",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Science, Books, Philosophy", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Office Drama"
        ),
        Profile(
            id = "seed_f_51", name = "Chetna Bansal", age = 25, height = "5'2\"",
            profession = "Student", location = "Bangalore", imageUrl = avatar(22),
            about = "Master's student in Data Science. Addicted to solving Rubik's cubes, indie cinema, and cold brews ☕🎲",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Tech & AI, Movies & TV, Gaming", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_f_52", name = "Mallika Sengupta", age = 28, height = "5'5\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = avatar(23),
            about = "Screenwriter developing quirky web series. Loves vinyl records, old bookstores, and rainy evenings 🌧️📼",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "26 - 33",
            preferredTopics = "Movies & TV, Books, Music", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats"
        ),
        Profile(
            id = "seed_f_53", name = "Pragati Chauhan", age = 24, height = "5'4\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = avatar(24),
            about = "Full-stack dev building scalable platforms. Big foodie who loves trying regional dosas and spicy curries 🥞💻",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Tech & AI, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work politics"
        ),
        Profile(
            id = "seed_f_54", name = "Namrata Nadkarni", age = 27, height = "5'7\"",
            profession = "Doctor / Healthcare", location = "Mumbai", imageUrl = avatar(25),
            about = "Psychiatrist who values emotional intelligence and authentic vulnerability. Loving soul music and ocean views 🌊🧠",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 33",
            preferredTopics = "Psychology, Philosophy, Music", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Politics"
        ),
        Profile(
            id = "seed_f_55", name = "Vasudha Kaul", age = 26, height = "5'5\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = avatar(26),
            about = "Kashmiri roots, Delhi hustle. Loves Kahwa tea, mountain getaways, and indie rock concerts ☕🏔️",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "24 - 30",
            preferredTopics = "Travel, Music, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office Drama"
        ),
        Profile(
            id = "seed_f_56", name = "Vidya Balasubramanian", age = 29, height = "5'3\"",
            profession = "Entrepreneur / Business", location = "Chennai", imageUrl = avatar(27),
            about = "Founder of a clean energy startup. Curious, driven, and loves weekend scuba diving trips 🤿⚡",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "27 - 35",
            preferredTopics = "Science, Tech & AI, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Religion"
        ),
        Profile(
            id = "seed_f_57", name = "Deepa Swaminathan", age = 25, height = "5'4\"",
            profession = "Finance / Banking", location = "Bangalore", imageUrl = avatar(0),
            about = "Risk consultant by day, passionate baker by weekend. Looking for calm, grounded souls to chat with 🍰📈",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Food & Cooking, Books, Fitness", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_f_58", name = "Mohini Ganguly", age = 23, height = "5'6\"",
            profession = "Student", location = "Pune", imageUrl = avatar(1),
            about = "Architecture student making models from recycled materials. Loves animated movies and street sketching 📐🎨",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "21 - 27",
            preferredTopics = "Architecture, Movies & TV, Art & Design", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Academic pressure"
        ),
        Profile(
            id = "seed_f_59", name = "Komal Rajput", age = 26, height = "5'5\"",
            profession = "Lawyer / Legal", location = "Jaipur", imageUrl = avatar(2),
            about = "Human rights advocate. Believes conversations should challenge us and expand our worldview ⚖️✨",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 32",
            preferredTopics = "Philosophy, Books, Psychology", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Materialism"
        ),
        Profile(
            id = "seed_f_60", name = "Barkha Saxena", age = 27, height = "5'4\"",
            profession = "Product / UX Designer", location = "Hyderabad", imageUrl = avatar(3),
            about = "Designing conversational AI tools. Love night bicycle rides, Irani chai, and old acoustic tunes 🚲☕",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Music, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics, Cynicism"
        )
    )

    val MALE_PROFILES: List<Profile> = listOf(
        Profile(
            id = "seed_m_01", name = "Arjun Kapoor", age = 27, height = "5'11\"",
            profession = "Product / UX Designer", location = "Bangalore", imageUrl = avatar(4),
            about = "Designing apps that don't stress people out. Coffee aficionado, weekend hiker in the Western Ghats ☕⛰️",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 30",
            preferredTopics = "Art & Design, Tech & AI, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Office Drama"
        ),
        Profile(
            id = "seed_m_02", name = "Aditya Patel", age = 26, height = "5'10\"",
            profession = "Software Engineer", location = "Pune", imageUrl = avatar(5),
            about = "Building scalable cloud infra. Big gamer, anime watcher, and amateur guitarist 🎸🎮",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office politics, Salary"
        ),
        Profile(
            id = "seed_m_03", name = "Rahul Verma", age = 28, height = "6'0\"",
            profession = "Doctor / Healthcare", location = "Delhi NCR", imageUrl = avatar(6),
            about = "Cardiologist who believes in keeping life simple. Loves early morning runs and soul-nourishing home meals 🏃‍♂️🍲",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Science, Fitness, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Past Relationships, Cynicism"
        ),
        Profile(
            id = "seed_m_04", name = "Rohan Reddy", age = 25, height = "5'9\"",
            profession = "Marketing / Growth", location = "Hyderabad", imageUrl = avatar(7),
            about = "Growth hacker with an insatiable appetite for authentic Hyderabadi biryani and Formula 1 races 🏎️🍛",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Food & Cooking, Travel, Gaming", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Religion"
        ),
        Profile(
            id = "seed_m_05", name = "Vikram Joshi", age = 29, height = "5'11\"",
            profession = "Architect", location = "Mumbai", imageUrl = avatar(8),
            about = "Restoring historical heritage facades. Sea breeze lover, vinyl collector, and street photographer 🏛️🌊",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "26 - 33",
            preferredTopics = "Architecture, Art & Design, Music", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chatter"
        ),
        Profile(
            id = "seed_m_06", name = "Siddharth Malhotra", age = 26, height = "6'1\"",
            profession = "Finance / Banking", location = "Mumbai", imageUrl = avatar(9),
            about = "Private equity analyst. Marathon runner and foodie who loves discovering secret basement pizzerias 🍕🏃",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Fitness, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Money brags, Stock tips"
        ),
        Profile(
            id = "seed_m_07", name = "Varun Iyer", age = 25, height = "5'10\"",
            profession = "Software Engineer", location = "Chennai", imageUrl = avatar(10),
            about = "Rust enthusiast. Loves classical Carnatic concerts, mechanical keyboards, and spicy podi idlis ⌨️☕",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Music, Science", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office stress"
        ),
        Profile(
            id = "seed_m_08", name = "Kunal Deshmukh", age = 27, height = "5'8\"",
            profession = "Teacher / Professor", location = "Pune", imageUrl = avatar(11),
            about = "Physics professor trying to make cosmos approachable. Stargazer and acoustic singer 🌌🎸",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Science, Philosophy, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Gossip"
        ),
        Profile(
            id = "seed_m_09", name = "Dev Singhania", age = 28, height = "6'0\"",
            profession = "Entrepreneur / Business", location = "Jaipur", imageUrl = avatar(12),
            about = "Building a clean-tech logistics venture. Passionate about sustainability, royal heritage, and coffee ☕🌿",
            gender = "Male", lookingFor = "Networking", preferredAgeRange = "26 - 34",
            preferredTopics = "Tech & AI, Travel, Books", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Status"
        ),
        Profile(
            id = "seed_m_10", name = "Aman Gupta", age = 24, height = "5'9\"",
            profession = "Writer / Content Creator", location = "Delhi NCR", imageUrl = avatar(13),
            about = "Humorist and video essayist. Believes a well-timed pun can diffuse any awkward situation 😂🎬",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Movies & TV, Music, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_m_11", name = "Nikhil Roy", age = 26, height = "5'10\"",
            profession = "Product / UX Designer", location = "Kolkata", imageUrl = avatar(14),
            about = "UX lead making interfaces accessible. Fond of old Calcutta trams, jazz saxophones, and dark roast coffee 🎷☕",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Art & Design, Music, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats"
        ),
        Profile(
            id = "seed_m_12", name = "Harsh Nair", age = 27, height = "5'11\"",
            profession = "Fitness / Wellness", location = "Kochi", imageUrl = avatar(15),
            about = "Surf instructor and calisthenics coach. Sunset swims, acoustic jams, and tropical smoothie bowls 🏄‍♂️🥥",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "24 - 31",
            preferredTopics = "Fitness, Travel, Music", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Cynicism, Drama"
        ),
        Profile(
            id = "seed_m_13", name = "Aryan Mehta", age = 23, height = "5'9\"",
            profession = "Student", location = "Bangalore", imageUrl = avatar(16),
            about = "AI student coding robotics vision. Weekend retro arcade gamer and enthusiast of spicy ramen 🍜🤖",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "21 - 27",
            preferredTopics = "Tech & AI, Gaming, Science", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_m_14", name = "Dhruv Rao", age = 28, height = "6'0\"",
            profession = "Lawyer / Legal", location = "Bangalore", imageUrl = avatar(17),
            about = "Tech policy legal consultant. Mountain trekker and collector of indie vinyl records 🏔️⚖️",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Philosophy, Books, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Court gossip, Politics"
        ),
        Profile(
            id = "seed_m_15", name = "Yash Saxena", age = 25, height = "5'10\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = avatar(18),
            about = "Game engine programmer building VR worlds. Love sci-fi novels and late-night highway chai stops ☕🌌",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Gaming, Tech & AI, Movies & TV", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office drama"
        ),
        Profile(
            id = "seed_m_16", name = "Sameer Menon", age = 29, height = "5'11\"",
            profession = "Doctor / Healthcare", location = "Goa", imageUrl = avatar(19),
            about = "Emergency physician who loves the sea. Living slowly, learning pottery, and playing blues harmonica 🌊🎶",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "26 - 34",
            preferredTopics = "Science, Art & Design, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital stress"
        ),
        Profile(
            id = "seed_m_17", name = "Kartik Kulkarni", age = 24, height = "5'8\"",
            profession = "Marketing / Growth", location = "Mumbai", imageUrl = avatar(20),
            about = "Digital brand marketer with a passion for indie films, spicy street chaat, and badminton 🏸🎬",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Movies & TV, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_m_18", name = "Rishabh Jain", age = 26, height = "5'10\"",
            profession = "Finance / Banking", location = "Delhi NCR", imageUrl = avatar(21),
            about = "Investment analyst who balances numbers with weekend pottery and specialty pour-overs ☕🏺",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Travel, Art & Design", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Salary brags"
        ),
        Profile(
            id = "seed_m_19", name = "Pranav Pillai", age = 27, height = "6'0\"",
            profession = "Architect", location = "Chandigarh", imageUrl = avatar(22),
            about = "Landscape architect designing urban greenery. Tree lover, cycling fanatic, and sourdough baker 🥖🌳",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Architecture, Fitness, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Politics"
        ),
        Profile(
            id = "seed_m_20", name = "Gaurav Mishra", age = 25, height = "5'9\"",
            profession = "Student", location = "Lucknow", imageUrl = avatar(23),
            about = "Master's student in International Relations. Loves Urdu poetry, chess, and Lucknowi kebabs ♟️🍢",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "22 - 28",
            preferredTopics = "Philosophy, Books, Food & Cooking", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Ex-partners"
        ),
        Profile(
            id = "seed_m_21", name = "Abhinav Das", age = 28, height = "5'11\"",
            profession = "Software Engineer", location = "Bangalore", imageUrl = avatar(24),
            about = "Distributed systems engineer. Weekend rock climber and lover of spicy street noodles 🧗‍♂️🍜",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Fitness, Travel", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work stress"
        ),
        Profile(
            id = "seed_m_22", name = "Ayush Bose", age = 24, height = "5'10\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = avatar(25),
            about = "Writing speculative sci-fi and exploring forgotten colonial buildings. Tea lover always ☕📖",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Books, Science, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Small talk"
        ),
        Profile(
            id = "seed_m_23", name = "Tushar Agarwal", age = 26, height = "5'9\"",
            profession = "Product / UX Designer", location = "Pune", imageUrl = avatar(26),
            about = "Obsessed with clean interface micro-interactions. Loving indie rock gigs and road trip playlists 🚗🎸",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Art & Design, Music, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial talk"
        ),
        Profile(
            id = "seed_m_24", name = "Manav Bhatt", age = 25, height = "6'0\"",
            profession = "Fitness / Wellness", location = "Dehradun", imageUrl = avatar(27),
            about = "Mountain guide and strength coach. Living in the Himalayan foothills with my golden retriever 🐕🏔️",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 30",
            preferredTopics = "Fitness, Travel, Philosophy", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "City complaints"
        ),
        Profile(
            id = "seed_m_25", name = "Mohit Hegde", age = 27, height = "5'10\"",
            profession = "Finance / Banking", location = "Bangalore", imageUrl = avatar(0),
            about = "Fintech product manager who loves playing drums and exploring authentic Udupi breakfast spots 🥁🥞",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Music, Tech & AI, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Salary, Politics"
        ),
        Profile(
            id = "seed_m_26", name = "Tarun Chopra", age = 29, height = "5'11\"",
            profession = "Entrepreneur / Business", location = "Delhi NCR", imageUrl = avatar(1),
            about = "Building a D2C specialty coffee brand. Passionate about roasting beans and world cinema ☕🎬",
            gender = "Male", lookingFor = "Networking", preferredAgeRange = "26 - 34",
            preferredTopics = "Food & Cooking, Movies & TV, Books", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Status, Gossip"
        ),
        Profile(
            id = "seed_m_27", name = "Vihaan Chawla", age = 24, height = "5'9\"",
            profession = "Student", location = "Chandigarh", imageUrl = avatar(2),
            about = "Studying law and human rights. Passionate debater, weekend cricketer, and loyal dog parent 🏏🐕",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Philosophy, Fitness, Books", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_m_28", name = "Shaurya Trivedi", age = 26, height = "6'1\"",
            profession = "Doctor / Healthcare", location = "Ahmedabad", imageUrl = avatar(3),
            about = "Orthopedic resident who loves staying active. Tennis player, weekend swimmer, and avid podcast listener 🎾🎧",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Science, Fitness, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital stories"
        ),
        Profile(
            id = "seed_m_29", name = "Mayank Sundaram", age = 28, height = "5'10\"",
            profession = "Software Engineer", location = "Chennai", imageUrl = avatar(4),
            about = "Compiler engineer fascinated by functional languages. Classical veena learner and chess enthusiast ♟️🎶",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Music, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk"
        ),
        Profile(
            id = "seed_m_30", name = "Chirag Mukherjee", age = 25, height = "5'8\"",
            profession = "Marketing / Growth", location = "Kolkata", imageUrl = avatar(5),
            about = "Storyteller for modern brands. Loves street sketching, indie gigs at Park Street, and mishti doi 🎨🍮",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Art & Design, Music, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_m_31", name = "Vivaan Varma", age = 27, height = "5'11\"",
            profession = "Architect", location = "Bangalore", imageUrl = avatar(6),
            about = "Designing sustainable timber structures. Loving morning bicycle rides in Cubbon Park and cold brews 🚲☕",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Architecture, Travel, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats"
        ),
        Profile(
            id = "seed_m_32", name = "Samarth Sethi", age = 26, height = "6'0\"",
            profession = "Finance / Banking", location = "Delhi NCR", imageUrl = avatar(7),
            about = "Quantitative researcher with a love for board games, sci-fi movies, and artisan sourdough baking 🎲🥖",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Gaming, Movies & TV, Science", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Stock tips, Money"
        ),
        Profile(
            id = "seed_m_33", name = "Tanmay Nambiar", age = 25, height = "5'10\"",
            profession = "Software Engineer", location = "Kochi", imageUrl = avatar(8),
            about = "Security researcher by day, sunset beach runner by evening. Passionate about digital privacy and jazz 🏃‍♂️🎷",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Fitness, Music", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office drama"
        ),
        Profile(
            id = "seed_m_34", name = "Neil Mathur", age = 24, height = "5'9\"",
            profession = "Product / UX Designer", location = "Hyderabad", imageUrl = avatar(9),
            about = "UI animation geek. Collecting vintage camera lenses and experimenting with homemade pasta recipes 🍝📷",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Art & Design, Food & Cooking, Tech & AI", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Gossip"
        ),
        Profile(
            id = "seed_m_35", name = "Reyansh Bhatia", age = 28, height = "6'1\"",
            profession = "Entrepreneur / Business", location = "Chandigarh", imageUrl = avatar(10),
            about = "Running a direct-to-consumer health foods brand. Fitness addict, road tripper, and upbeat optimist 🏋️‍♂️🛣️",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 33",
            preferredTopics = "Fitness, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Drama, Cynicism"
        ),
        Profile(
            id = "seed_m_36", name = "Anirudh Rawat", age = 26, height = "5'11\"",
            profession = "Teacher / Professor", location = "Dehradun", imageUrl = avatar(11),
            about = "Teaching literature with mountain views outside the classroom window. Stargazing and acoustic guitar 🌌🎸",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Philosophy, Music", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Politics"
        ),
        Profile(
            id = "seed_m_37", name = "Agastya Dutta", age = 25, height = "5'8\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = avatar(12),
            about = "Scriptwriter and podcast host covering indie music. Huge fan of Satyajit Ray classics and street tea ☕🎬",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Movies & TV, Music, Books", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_m_38", name = "Parth Parekh", age = 27, height = "5'10\"",
            profession = "Lawyer / Legal", location = "Mumbai", imageUrl = avatar(13),
            about = "Maritime law attorney. Loves weekend sailing at Gateway, jazz clubs in Bandra, and historical fiction ⛵🎷",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Books, Travel, Philosophy", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics"
        ),
        Profile(
            id = "seed_m_39", name = "Advait Goswami", age = 24, height = "5'9\"",
            profession = "Student", location = "Jaipur", imageUrl = avatar(14),
            about = "Studying medieval architecture. Passionate about heritage conservation and film photography 📸🏛️",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Architecture, Art & Design, Travel", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Academic stress"
        ),
        Profile(
            id = "seed_m_40", name = "Ishan Pandey", age = 26, height = "6'0\"",
            profession = "Doctor / Healthcare", location = "Lucknow", imageUrl = avatar(15),
            about = "Neurologist in training. Fascinated by human consciousness, chess, and Lucknow's royal street food 🧠🍢",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Science, Philosophy, Food & Cooking", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital complaints"
        ),
        Profile(
            id = "seed_m_41", name = "Raghav Ghosh", age = 28, height = "5'11\"",
            profession = "Software Engineer", location = "Pune", imageUrl = avatar(16),
            about = "Building embedded robotics firmware. Amateur triathlete and weekend barbecue grill master 🚴‍♂️🍖",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Fitness, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work politics"
        ),
        Profile(
            id = "seed_m_42", name = "Shreyas Grover", age = 25, height = "5'10\"",
            profession = "Product / UX Designer", location = "Delhi NCR", imageUrl = avatar(17),
            about = "Design lead curious about behavioral economics. Loves vintage watches, coffee roasting, and museums ⌚☕",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "23 - 30",
            preferredTopics = "Art & Design, Psychology, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial banter"
        ),
        Profile(
            id = "seed_m_43", name = "Tejas Shukla", age = 27, height = "5'9\"",
            profession = "Marketing / Growth", location = "Bangalore", imageUrl = avatar(18),
            about = "Product marketing lead by day, indie stand-up comic by night. Let's trade witty one-liners 🎤😄",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "24 - 31",
            preferredTopics = "Movies & TV, Music, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Religion"
        ),
        Profile(
            id = "seed_m_44", name = "Vedant Sood", age = 26, height = "6'1\"",
            profession = "Finance / Banking", location = "Mumbai", imageUrl = avatar(19),
            about = "Risk analyst with a passion for high-altitude trekking, rock climbing, and spicy street ramen 🧗‍♂️🍜",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Fitness, Travel, Movies & TV", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Salary talk"
        ),
        Profile(
            id = "seed_m_45", name = "Ojas Somani", age = 24, height = "5'10\"",
            profession = "Student", location = "Ahmedabad", imageUrl = avatar(20),
            about = "MBA candidate with a background in design. Big board game enthusiast and amateur barista ☕🎲",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Gaming, Tech & AI, Art & Design", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_m_46", name = "Nakul Agnihotri", age = 29, height = "5'11\"",
            profession = "Architect", location = "Bhopal", imageUrl = avatar(21),
            about = "Ecological architect designing passive solar homes. Passionate about pottery and acoustic folk music 🏺🎶",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "26 - 34",
            preferredTopics = "Architecture, Science, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chatter"
        ),
        Profile(
            id = "seed_m_47", name = "Ayan Bansal", age = 25, height = "5'9\"",
            profession = "Software Engineer", location = "Bangalore", imageUrl = avatar(22),
            about = "Full-stack developer building real-time applications. Passionate about badminton and specialty coffee 🏸☕",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office drama"
        ),
        Profile(
            id = "seed_m_48", name = "Samar Sengupta", age = 27, height = "6'0\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = avatar(23),
            about = "Novel writer and tea enthusiast. Loves book clubs, rainy train journeys, and meaningful late-night talks 🚂📖",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Books, Philosophy, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Small talk"
        ),
        Profile(
            id = "seed_m_49", name = "Hrithik Chauhan", age = 24, height = "5'10\"",
            profession = "Fitness / Wellness", location = "Hyderabad", imageUrl = avatar(24),
            about = "Strength and conditioning coach. Weekend swimmer, foodie at heart, and lover of acoustic tunes 🏊‍♂️🍛",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Fitness, Food & Cooking, Music", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Cynicism"
        ),
        Profile(
            id = "seed_m_50", name = "Krrish Nadkarni", age = 28, height = "5'11\"",
            profession = "Doctor / Healthcare", location = "Mumbai", imageUrl = avatar(25),
            about = "Pediatric surgeon who relaxes by playing blues piano and taking sunset ferry rides to Alibaug 🎹⛴️",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 33",
            preferredTopics = "Music, Science, Travel", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital politics"
        ),
        Profile(
            id = "seed_m_51", name = "Ranveer Kaul", age = 26, height = "6'0\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = avatar(26),
            about = "Growth strategist who loves exploring historic Delhi alleys on bicycle and catching indie rock gigs 🚲🎸",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "24 - 31",
            preferredTopics = "Music, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics"
        ),
        Profile(
            id = "seed_m_52", name = "Shaheer Swaminathan", age = 27, height = "5'10\"",
            profession = "Lawyer / Legal", location = "Chennai", imageUrl = avatar(27),
            about = "Constitutional law researcher. Coffee aficionado, weekend tennis player, and classical music lover 🎾☕",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Philosophy, Books, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Court gossip"
        ),
        Profile(
            id = "seed_m_53", name = "Zayd Ganguly", age = 25, height = "5'9\"",
            profession = "Product / UX Designer", location = "Bangalore", imageUrl = avatar(0),
            about = "Designing thoughtful digital interactions. Weekend street photographer, film buff, and sourdough baker 📸🥖",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Art & Design, Movies & TV, Food & Cooking", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial talk"
        ),
        Profile(
            id = "seed_m_54", name = "Armaan Rajput", age = 28, height = "6'1\"",
            profession = "Entrepreneur / Business", location = "Jaipur", imageUrl = avatar(1),
            about = "Building a craft brewery showcasing local Indian ingredients. Heritage lover and desert explorer 🍺🏜️",
            gender = "Male", lookingFor = "Networking", preferredAgeRange = "26 - 34",
            preferredTopics = "Food & Cooking, Travel, Tech & AI", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Status"
        ),
        Profile(
            id = "seed_m_55", name = "Farhan Saxena", age = 24, height = "5'10\"",
            profession = "Student", location = "Pune", imageUrl = avatar(2),
            about = "Master's student in Environmental Engineering. Passionate about clean rivers, cycling, and indie podcasts 🚴‍♂️🎙️",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Science, Travel, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_m_56", name = "Vivian D'Souza", age = 27, height = "5'11\"",
            profession = "Teacher / Professor", location = "Goa", imageUrl = avatar(3),
            about = "High school music teacher and acoustic guitarist. Sunset beach walks, fresh seafood, and soulful vibes 🎸🌊",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Music, Philosophy, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "City rush complaints"
        ),
        Profile(
            id = "seed_m_57", name = "Alok Sen", age = 26, height = "5'9\"",
            profession = "Finance / Banking", location = "Kolkata", imageUrl = avatar(4),
            about = "Financial analyst with a passion for Bengali theatre, playing chess, and exploring heritage bookstalls 🎭♟️",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Art & Design, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Salary talk"
        ),
        Profile(
            id = "seed_m_58", name = "Madhav Sharma", age = 25, height = "5'10\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = avatar(5),
            about = "Cybersecurity engineer obsessed with cryptography puzzles. Loves trying spicy biryanis and playing badminton 🏸💻",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work politics"
        ),
        Profile(
            id = "seed_m_59", name = "Kabir Deshmukh", age = 28, height = "6'0\"",
            profession = "Architect", location = "Mumbai", imageUrl = avatar(6),
            about = "Architectural photographer and urban sketcher. Big fan of Marine Drive evening breezes and black coffee ☕🌊",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 33",
            preferredTopics = "Architecture, Art & Design, Travel", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Gossip"
        ),
        Profile(
            id = "seed_m_60", name = "Rishi Mehra", age = 26, height = "5'11\"",
            profession = "Product / UX Designer", location = "Bangalore", imageUrl = avatar(7),
            about = "Product designer crafting calm technology experiences. Weekend vinyl listener and espresso tinkerer ☕🎶",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Art & Design, Music, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats, Office Drama"
        )
    )

    fun getSeedProfiles(userGender: String?, shuffled: Boolean = false): List<Profile> {
        val list = when {
            userGender.equals("Male", ignoreCase = true) -> FEMALE_PROFILES
            userGender.equals("Female", ignoreCase = true) -> MALE_PROFILES
            else -> FEMALE_PROFILES + MALE_PROFILES
        }
        return if (shuffled) list.shuffled() else list
    }

    fun getProfileById(id: String): Profile? =
        (FEMALE_PROFILES + MALE_PROFILES).firstOrNull { it.id == id }

    fun isSeedProfile(id: String?): Boolean =
        id?.startsWith("seed_") == true
}
