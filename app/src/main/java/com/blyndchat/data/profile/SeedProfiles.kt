package com.blyndchat.data.profile

import com.blyndchat.data.model.Profile

/**
 * 60 authentic Indian profiles for each gender with diverse names, professions,
 * cities, engaging bios, and chat preferences.
 */
object SeedProfiles {

    private fun femaleAvatar(index: Int): String {
        val num = (index % 10) + 1
        return "file:///android_asset/avatars/female_$num.png"
    }

    private fun maleAvatar(index: Int): String {
        val num = (index % 10) + 1
        return "file:///android_asset/avatars/male_$num.png"
    }

    val FEMALE_PROFILES: List<Profile> = listOf(
        Profile(
            id = "seed_f_01", name = "Ananya Sharma", age = 25, height = "5'4\"",
            profession = "Product / UX Designer", location = "Bangalore", imageUrl = femaleAvatar(0),
            about = "chai > coffee always. mostly at cafes in indiranagar",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 30",
            preferredTopics = "Art & Design, Music, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_f_02", name = "Diya Patel", age = 24, height = "5'3\"",
            profession = "Software Engineer", location = "Pune", imageUrl = femaleAvatar(1),
            about = "android dev by day, gamer by night. tell me your favorite anime 🎮",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Tech & AI, Gaming, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office Drama, Salary"
        ),
        Profile(
            id = "seed_f_03", name = "Sneha Iyer", age = 27, height = "5'5\"",
            profession = "Doctor / Healthcare", location = "Chennai", imageUrl = femaleAvatar(2),
            about = "doctor with weird working hours. filter coffee keeps me going",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "26 - 32",
            preferredTopics = "Science, Music, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats, Cynicism"
        ),
        Profile(
            id = "seed_f_04", name = "Pooja Verma", age = 26, height = "5'6\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = femaleAvatar(3),
            about = "fluent in sarcasm. tell me your top momo spot in delhi 🥟",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "24 - 30",
            preferredTopics = "Food & Cooking, Travel, Psychology", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships, Religion"
        ),
        Profile(
            id = "seed_f_05", name = "Riya Sen", age = 23, height = "5'2\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = femaleAvatar(4),
            about = "college street, books and too much chai",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "22 - 28",
            preferredTopics = "Books, Philosophy, Art & Design", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Money"
        ),
        Profile(
            id = "seed_f_06", name = "Tanvi Deshmukh", age = 28, height = "5'7\"",
            profession = "Architect", location = "Mumbai", imageUrl = femaleAvatar(5),
            about = "architect in mumbai. marine drive sunsets > everything",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "26 - 33",
            preferredTopics = "Architecture, Travel, Fitness", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics, Negativity"
        ),
        Profile(
            id = "seed_f_07", name = "Ishita Roy", age = 25, height = "5'4\"",
            profession = "Finance / Banking", location = "Mumbai", imageUrl = femaleAvatar(6),
            about = "spreadsheets on weekdays, live gigs on weekends",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "24 - 30",
            preferredTopics = "Music, Movies & TV, Travel", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Salary, Drama"
        ),
        Profile(
            id = "seed_f_08", name = "Meera Nair", age = 26, height = "5'5\"",
            profession = "Teacher / Professor", location = "Kochi", imageUrl = femaleAvatar(7),
            about = "teaching literature and waiting for monsoon season 🌧️",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Science, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Office Drama"
        ),
        Profile(
            id = "seed_f_09", name = "Shreya Joshi", age = 24, height = "5'3\"",
            profession = "Student", location = "Pune", imageUrl = femaleAvatar(8),
            about = "psych grad. down for late night deep talks and chai",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "22 - 27",
            preferredTopics = "Psychology, Philosophy, Science", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Ex-partners, Small talk"
        ),
        Profile(
            id = "seed_f_10", name = "Neha Gupta", age = 29, height = "5'6\"",
            profession = "Entrepreneur / Business", location = "Jaipur", imageUrl = femaleAvatar(9),
            about = "running a small clothing brand. love good coffee and quiet spots ☕",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "27 - 35",
            preferredTopics = "Art & Design, Travel, Tech & AI", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_f_11", name = "Kavya Reddy", age = 25, height = "5'5\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = femaleAvatar(10),
            about = "always down for paradise or bawarchi biryani 🍛",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Food & Cooking, Gaming", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office Drama"
        ),
        Profile(
            id = "seed_f_12", name = "Aditi Rao", age = 26, height = "5'4\"",
            profession = "Lawyer / Legal", location = "Bangalore", imageUrl = femaleAvatar(11),
            about = "corporate lawyer, asleep by 11pm. dog lover 🐕",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Books, Travel, Psychology", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Court stories, Politics"
        ),
        Profile(
            id = "seed_f_13", name = "Simran Kaur", age = 24, height = "5'7\"",
            profession = "Fitness / Wellness", location = "Chandigarh", imageUrl = femaleAvatar(12),
            about = "morning runs, good food and bad puns",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Fitness, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Salary, Drama"
        ),
        Profile(
            id = "seed_f_14", name = "Priyanka Mehta", age = 28, height = "5'5\"",
            profession = "Product / UX Designer", location = "Ahmedabad", imageUrl = femaleAvatar(13),
            about = "designer who bakes way too much sourdough",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "26 - 32",
            preferredTopics = "Art & Design, Food & Cooking, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial banter"
        ),
        Profile(
            id = "seed_f_15", name = "Anika Malhotra", age = 23, height = "5'4\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = femaleAvatar(14),
            about = "will probably judge your spotify playlist",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "21 - 27",
            preferredTopics = "Music, Movies & TV, Art & Design", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Religion"
        ),
        Profile(
            id = "seed_f_16", name = "Nidhi Agarwal", age = 27, height = "5'3\"",
            profession = "Finance / Banking", location = "Kolkata", imageUrl = femaleAvatar(15),
            about = "just looking for good conversation and maybe some street food",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Music, Travel, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Past Relationships"
        ),
        Profile(
            id = "seed_f_17", name = "Kriti Saxena", age = 25, height = "5'6\"",
            profession = "Software Engineer", location = "Bangalore", imageUrl = femaleAvatar(16),
            about = "coder who needs to touch grass more often",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "24 - 30",
            preferredTopics = "Tech & AI, Science, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Office Drama"
        ),
        Profile(
            id = "seed_f_18", name = "Shruti Menon", age = 26, height = "5'5\"",
            profession = "Architect", location = "Goa", imageUrl = femaleAvatar(17),
            about = "moved to goa and never leaving 🌊",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Architecture, Travel, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "City hustle complaints"
        ),
        Profile(
            id = "seed_f_19", name = "Pallavi Kulkarni", age = 29, height = "5'4\"",
            profession = "Doctor / Healthcare", location = "Mumbai", imageUrl = femaleAvatar(18),
            about = "pediatrician. coffee keeps me alive during 24h duties ☕",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "27 - 34",
            preferredTopics = "Fitness, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital stress"
        ),
        Profile(
            id = "seed_f_20", name = "Aayushi Jain", age = 24, height = "5'2\"",
            profession = "Student", location = "Jaipur", imageUrl = femaleAvatar(19),
            about = "history student. tell me an interesting fact about your city",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Books, Art & Design, Travel", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exam stress, Money"
        ),
        Profile(
            id = "seed_f_21", name = "Radhika Pillai", age = 26, height = "5'5\"",
            profession = "Writer / Content Creator", location = "Bangalore", imageUrl = femaleAvatar(20),
            about = "mostly reading, writing or overthinking",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Tech & AI, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Office Drama"
        ),
        Profile(
            id = "seed_f_22", name = "Swati Mishra", age = 25, height = "5'4\"",
            profession = "Marketing / Growth", location = "Lucknow", imageUrl = femaleAvatar(21),
            about = "kebab enthusiast. 10/10 recommendation guaranteed 🍢",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Food & Cooking, Movies & TV, Music", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Past Relationships"
        ),
        Profile(
            id = "seed_f_23", name = "Roshni Das", age = 27, height = "5'6\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = femaleAvatar(22),
            about = "late night drives, old hindi songs and cold coffee",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Gaming, Travel", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work complaints, Salary"
        ),
        Profile(
            id = "seed_f_24", name = "Natasha Bose", age = 24, height = "5'5\"",
            profession = "Product / UX Designer", location = "Kolkata", imageUrl = femaleAvatar(23),
            about = "design, indie movies, and lazy sundays",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Movies & TV, Music, Art & Design", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial talk"
        ),
        Profile(
            id = "seed_f_25", name = "Preeti Singhania", age = 28, height = "5'3\"",
            profession = "Entrepreneur / Business", location = "Delhi NCR", imageUrl = femaleAvatar(24),
            about = "running a startup. always tired but always down for food",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "26 - 34",
            preferredTopics = "Science, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_f_26", name = "Shalini Bhatt", age = 25, height = "5'7\"",
            profession = "Teacher / Professor", location = "Dehradun", imageUrl = femaleAvatar(25),
            about = "mountains > beaches any day 🏔️",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 30",
            preferredTopics = "Books, Travel, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "City traffic, Cynicism"
        ),
        Profile(
            id = "seed_f_27", name = "Divya Hegde", age = 26, height = "5'4\"",
            profession = "Finance / Banking", location = "Bangalore", imageUrl = femaleAvatar(26),
            about = "badminton on weekends? 🏸",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "24 - 30",
            preferredTopics = "Music, Fitness, Movies & TV", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Stock tips, Work stress"
        ),
        Profile(
            id = "seed_f_28", name = "Rhea Kapoor", age = 23, height = "5'5\"",
            profession = "Student", location = "Mumbai", imageUrl = femaleAvatar(27),
            about = "media student. here for good banter and boba",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "21 - 26",
            preferredTopics = "Movies & TV, Music, Art & Design", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Academic pressure"
        ),
        Profile(
            id = "seed_f_29", name = "Sanya Chawla", age = 27, height = "5'6\"",
            profession = "Lawyer / Legal", location = "Chandigarh", imageUrl = femaleAvatar(0),
            about = "lawyer, but don't hold that against me",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Books, Philosophy, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Past Relationships, Gossip"
        ),
        Profile(
            id = "seed_f_30", name = "Bhavna Trivedi", age = 25, height = "5'3\"",
            profession = "Software Engineer", location = "Ahmedabad", imageUrl = femaleAvatar(1),
            about = "cloud engineer. i make really good banana bread",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Food & Cooking", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics"
        ),
        Profile(
            id = "seed_f_31", name = "Tara Sundaram", age = 26, height = "5'5\"",
            profession = "Doctor / Healthcare", location = "Chennai", imageUrl = femaleAvatar(2),
            about = "doc. tell me what podcast you're listening to right now",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 31",
            preferredTopics = "Science, Art & Design, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Work stress"
        ),
        Profile(
            id = "seed_f_32", name = "Trisha Mukherjee", age = 24, height = "5'4\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = femaleAvatar(3),
            about = "food, music and documenting quiet corners of the city",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Food & Cooking, Music, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics"
        ),
        Profile(
            id = "seed_f_33", name = "Alisha Varma", age = 28, height = "5'6\"",
            profession = "Architect", location = "Bangalore", imageUrl = femaleAvatar(4),
            about = "plant mom trying not to kill another succulent 🪴",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "26 - 33",
            preferredTopics = "Architecture, Music, Philosophy", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Gossip"
        ),
        Profile(
            id = "seed_f_34", name = "Payal Sethi", age = 25, height = "5'2\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = femaleAvatar(5),
            about = "fluent in sarcasm, bad at texting first",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Movies & TV, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_f_35", name = "Karishma Chopra", age = 27, height = "5'7\"",
            profession = "Product / UX Designer", location = "Mumbai", imageUrl = femaleAvatar(6),
            about = "bandstand sunsets and iced lattes",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Psychology, Art & Design, Tech & AI", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics"
        ),
        Profile(
            id = "seed_f_36", name = "Avani Kothari", age = 24, height = "5'3\"",
            profession = "Finance / Banking", location = "Pune", imageUrl = femaleAvatar(7),
            about = "finance by day, dancer on weekends",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "23 - 29",
            preferredTopics = "Art & Design, Music, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Money brags"
        ),
        Profile(
            id = "seed_f_37", name = "Lavanya Nambiar", age = 26, height = "5'5\"",
            profession = "Teacher / Professor", location = "Kochi", imageUrl = femaleAvatar(8),
            about = "math teacher. love rainy days and a good mystery book 🌧️",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "24 - 30",
            preferredTopics = "Books, Science, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Drama, Gossip"
        ),
        Profile(
            id = "seed_f_38", name = "Sanjana Mathur", age = 25, height = "5'4\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = femaleAvatar(9),
            about = "probably playing with dogs or rewatching brooklyn nine-nine",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Movies & TV", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work stress"
        ),
        Profile(
            id = "seed_f_39", name = "Parul Bhatia", age = 29, height = "5'6\"",
            profession = "Entrepreneur / Business", location = "Chandigarh", imageUrl = femaleAvatar(10),
            about = "cafe owner. coffee is on me if you can make me laugh",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "27 - 35",
            preferredTopics = "Food & Cooking, Travel, Books", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Religion"
        ),
        Profile(
            id = "seed_f_40", name = "Diksha Rawat", age = 23, height = "5'4\"",
            profession = "Student", location = "Dehradun", imageUrl = femaleAvatar(11),
            about = "happiest outdoors in the woods 🌲",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "21 - 27",
            preferredTopics = "Science, Music, Travel", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "City gossip"
        ),
        Profile(
            id = "seed_f_41", name = "Sonali Dutta", age = 26, height = "5'5\"",
            profession = "Doctor / Healthcare", location = "Kolkata", imageUrl = femaleAvatar(12),
            about = "surgery resident. sleep deprived but still smiling",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Science, Fitness, Philosophy", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Medical complaints"
        ),
        Profile(
            id = "seed_f_42", name = "Mansi Parekh", age = 27, height = "5'3\"",
            profession = "Lawyer / Legal", location = "Mumbai", imageUrl = femaleAvatar(13),
            about = "theatre, kala ghoda exhibitions and strong coffee ☕",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Art & Design, Books, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Office Drama"
        ),
        Profile(
            id = "seed_f_43", name = "Urvashi Goswami", age = 25, height = "5'6\"",
            profession = "Writer / Content Creator", location = "Jaipur", imageUrl = femaleAvatar(14),
            about = "travel writer. show me your favorite hidden spot in town",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "23 - 30",
            preferredTopics = "Travel, Art & Design, Books", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chatter"
        ),
        Profile(
            id = "seed_f_44", name = "Akanksha Pandey", age = 24, height = "5'4\"",
            profession = "Marketing / Growth", location = "Bangalore", imageUrl = femaleAvatar(15),
            about = "huge board game nerd. let's play catan 🎲",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Tech & AI, Gaming, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office politics"
        ),
        Profile(
            id = "seed_f_45", name = "Ishani Ghosh", age = 28, height = "5'5\"",
            profession = "Software Engineer", location = "Pune", imageUrl = femaleAvatar(16),
            about = "devops. pani puri can solve 90% of my problems",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "26 - 33",
            preferredTopics = "Tech & AI, Fitness, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Ex-partners"
        ),
        Profile(
            id = "seed_f_46", name = "Garima Grover", age = 26, height = "5'7\"",
            profession = "Product / UX Designer", location = "Delhi NCR", imageUrl = femaleAvatar(17),
            about = "thrifting, museum visits, and iced matcha 🍵",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Art & Design, Psychology, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_f_47", name = "Juhi Shukla", age = 25, height = "5'3\"",
            profession = "Finance / Banking", location = "Lucknow", imageUrl = femaleAvatar(18),
            about = "lucknow foodie. will travel anywhere for good desserts",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Food & Cooking, Music, Books", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Salary, Status"
        ),
        Profile(
            id = "seed_f_48", name = "Nikita Sood", age = 24, height = "5'5\"",
            profession = "Fitness / Wellness", location = "Chandigarh", imageUrl = femaleAvatar(19),
            about = "pilates trainer. 6am workout person (yes we exist) 🙃",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Fitness, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Drama, Cynicism"
        ),
        Profile(
            id = "seed_f_49", name = "Esha Somani", age = 27, height = "5'4\"",
            profession = "Architect", location = "Ahmedabad", imageUrl = femaleAvatar(20),
            about = "urban planning and pottery. simple things",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Architecture, Art & Design, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk"
        ),
        Profile(
            id = "seed_f_50", name = "Ruchi Agnihotri", age = 26, height = "5'6\"",
            profession = "Teacher / Professor", location = "Bhopal", imageUrl = femaleAvatar(21),
            about = "chemistry teacher. evenings by upper lake are unmatched",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Science, Books, Philosophy", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Office Drama"
        ),
        Profile(
            id = "seed_f_51", name = "Chetna Bansal", age = 25, height = "5'2\"",
            profession = "Student", location = "Bangalore", imageUrl = femaleAvatar(22),
            about = "data science student surviving on cold brew ☕",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Tech & AI, Movies & TV, Gaming", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_f_52", name = "Mallika Sengupta", age = 28, height = "5'5\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = femaleAvatar(23),
            about = "writing scripts and collecting old records",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "26 - 33",
            preferredTopics = "Movies & TV, Books, Music", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats"
        ),
        Profile(
            id = "seed_f_53", name = "Pragati Chauhan", age = 24, height = "5'4\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = femaleAvatar(24),
            about = "coder who lives for crispy butter masala dosa",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Tech & AI, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work politics"
        ),
        Profile(
            id = "seed_f_54", name = "Namrata Nadkarni", age = 27, height = "5'7\"",
            profession = "Doctor / Healthcare", location = "Mumbai", imageUrl = femaleAvatar(25),
            about = "psychiatrist. here for genuine conversations, no small talk",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 33",
            preferredTopics = "Psychology, Philosophy, Music", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Politics"
        ),
        Profile(
            id = "seed_f_55", name = "Vasudha Kaul", age = 26, height = "5'5\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = femaleAvatar(26),
            about = "kashmiri kahwa > everything. take me to the mountains 🏔️",
            gender = "Female", lookingFor = "Casual chat", preferredAgeRange = "24 - 30",
            preferredTopics = "Travel, Music, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office Drama"
        ),
        Profile(
            id = "seed_f_56", name = "Vidya Balasubramanian", age = 29, height = "5'3\"",
            profession = "Entrepreneur / Business", location = "Chennai", imageUrl = femaleAvatar(27),
            about = "building a climate startup. weekend scuba diver 🤿",
            gender = "Female", lookingFor = "Networking", preferredAgeRange = "27 - 35",
            preferredTopics = "Science, Tech & AI, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Religion"
        ),
        Profile(
            id = "seed_f_57", name = "Deepa Swaminathan", age = 25, height = "5'4\"",
            profession = "Finance / Banking", location = "Bangalore", imageUrl = femaleAvatar(0),
            about = "risk consultant who bakes cookies when stressed 🍪",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Food & Cooking, Books, Fitness", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_f_58", name = "Mohini Ganguly", age = 23, height = "5'6\"",
            profession = "Student", location = "Pune", imageUrl = femaleAvatar(1),
            about = "architecture student. mostly sleep deprived and sketching",
            gender = "Female", lookingFor = "Friendship", preferredAgeRange = "21 - 27",
            preferredTopics = "Architecture, Movies & TV, Art & Design", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Academic pressure"
        ),
        Profile(
            id = "seed_f_59", name = "Komal Rajput", age = 26, height = "5'5\"",
            profession = "Lawyer / Legal", location = "Jaipur", imageUrl = femaleAvatar(2),
            about = "lawyer. love people who can hold a passionate debate",
            gender = "Female", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 32",
            preferredTopics = "Philosophy, Books, Psychology", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Materialism"
        ),
        Profile(
            id = "seed_f_60", name = "Barkha Saxena", age = 27, height = "5'4\"",
            profession = "Product / UX Designer", location = "Hyderabad", imageUrl = femaleAvatar(3),
            about = "irani chai and late night bicycle rides",
            gender = "Female", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Music, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics, Cynicism"
        )
    )

    val MALE_PROFILES: List<Profile> = listOf(
        Profile(
            id = "seed_m_01", name = "Arjun Kapoor", age = 27, height = "5'11\"",
            profession = "Product / UX Designer", location = "Bangalore", imageUrl = maleAvatar(4),
            about = "product designer. mostly working from cafes or in western ghats",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 30",
            preferredTopics = "Art & Design, Tech & AI, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Office Drama"
        ),
        Profile(
            id = "seed_m_02", name = "Aditya Patel", age = 26, height = "5'10\"",
            profession = "Software Engineer", location = "Pune", imageUrl = maleAvatar(5),
            about = "coder. anime, gaming and bad guitar playing 🎸",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office politics, Salary"
        ),
        Profile(
            id = "seed_m_03", name = "Rahul Verma", age = 28, height = "6'0\"",
            profession = "Doctor / Healthcare", location = "Delhi NCR", imageUrl = maleAvatar(6),
            about = "cardiologist. simple guy, love running and good home food",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Science, Fitness, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Past Relationships, Cynicism"
        ),
        Profile(
            id = "seed_m_04", name = "Rohan Reddy", age = 25, height = "5'9\"",
            profession = "Marketing / Growth", location = "Hyderabad", imageUrl = maleAvatar(7),
            about = "f1 sundays and hyderabadi biryani 🏎️",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Food & Cooking, Travel, Gaming", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Religion"
        ),
        Profile(
            id = "seed_m_05", name = "Vikram Joshi", age = 29, height = "5'11\"",
            profession = "Architect", location = "Mumbai", imageUrl = maleAvatar(8),
            about = "architect in mumbai. mostly just taking photos of old buildings",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "26 - 33",
            preferredTopics = "Architecture, Art & Design, Music", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chatter"
        ),
        Profile(
            id = "seed_m_06", name = "Siddharth Malhotra", age = 26, height = "6'1\"",
            profession = "Finance / Banking", location = "Mumbai", imageUrl = maleAvatar(9),
            about = "running half marathons so i can eat more pizza 🍕",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Fitness, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Money brags, Stock tips"
        ),
        Profile(
            id = "seed_m_07", name = "Varun Iyer", age = 25, height = "5'10\"",
            profession = "Software Engineer", location = "Chennai", imageUrl = maleAvatar(10),
            about = "mechanical keyboards, rust, and spicy podi idli",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Music, Science", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office stress"
        ),
        Profile(
            id = "seed_m_08", name = "Kunal Deshmukh", age = 27, height = "5'8\"",
            profession = "Teacher / Professor", location = "Pune", imageUrl = maleAvatar(11),
            about = "physics teacher. ask me anything about space",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Science, Philosophy, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Gossip"
        ),
        Profile(
            id = "seed_m_09", name = "Dev Singhania", age = 28, height = "6'0\"",
            profession = "Entrepreneur / Business", location = "Jaipur", imageUrl = maleAvatar(12),
            about = "building a startup. here to meet interesting people",
            gender = "Male", lookingFor = "Networking", preferredAgeRange = "26 - 34",
            preferredTopics = "Tech & AI, Travel, Books", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "Politics, Status"
        ),
        Profile(
            id = "seed_m_10", name = "Aman Gupta", age = 24, height = "5'9\"",
            profession = "Writer / Content Creator", location = "Delhi NCR", imageUrl = maleAvatar(13),
            about = "can make decent pasta and terrible dad jokes",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Movies & TV, Music, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_m_11", name = "Nikhil Roy", age = 26, height = "5'10\"",
            profession = "Product / UX Designer", location = "Kolkata", imageUrl = maleAvatar(14),
            about = "ux design / jazz / dark roast coffee ☕",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Art & Design, Music, Books", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats"
        ),
        Profile(
            id = "seed_m_12", name = "Harsh Nair", age = 27, height = "5'11\"",
            profession = "Fitness / Wellness", location = "Kochi", imageUrl = maleAvatar(15),
            about = "surf coach. sunsets in kochi never get old 🏄‍♂️",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "24 - 31",
            preferredTopics = "Fitness, Travel, Music", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Cynicism, Drama"
        ),
        Profile(
            id = "seed_m_13", name = "Aryan Mehta", age = 23, height = "5'9\"",
            profession = "Student", location = "Bangalore", imageUrl = maleAvatar(16),
            about = "just a student trying to figure life out tbh",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "21 - 27",
            preferredTopics = "Tech & AI, Gaming, Science", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_m_14", name = "Dhruv Rao", age = 28, height = "6'0\"",
            profession = "Lawyer / Legal", location = "Bangalore", imageUrl = maleAvatar(17),
            about = "tech policy by day, vinyl records by night",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Philosophy, Books, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Court gossip, Politics"
        ),
        Profile(
            id = "seed_m_15", name = "Yash Saxena", age = 25, height = "5'10\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = maleAvatar(18),
            about = "game dev. late night chai drives are my therapy ☕",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Gaming, Tech & AI, Movies & TV", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office drama"
        ),
        Profile(
            id = "seed_m_16", name = "Sameer Menon", age = 29, height = "5'11\"",
            profession = "Doctor / Healthcare", location = "Goa", imageUrl = maleAvatar(19),
            about = "doctor who moved to goa for the slow life 🌊",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "26 - 34",
            preferredTopics = "Science, Art & Design, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital stress"
        ),
        Profile(
            id = "seed_m_17", name = "Kartik Kulkarni", age = 24, height = "5'8\"",
            profession = "Marketing / Growth", location = "Mumbai", imageUrl = maleAvatar(20),
            about = "badminton and street chaat. down for weekend plans",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Movies & TV, Food & Cooking, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_m_18", name = "Rishabh Jain", age = 26, height = "5'10\"",
            profession = "Finance / Banking", location = "Delhi NCR", imageUrl = maleAvatar(21),
            about = "analyst by profession, coffee nerd by choice",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Travel, Art & Design", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Salary brags"
        ),
        Profile(
            id = "seed_m_19", name = "Pranav Pillai", age = 27, height = "6'0\"",
            profession = "Architect", location = "Chandigarh", imageUrl = maleAvatar(22),
            about = "cycling around the city and baking sourdough",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Architecture, Fitness, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Politics"
        ),
        Profile(
            id = "seed_m_20", name = "Gaurav Mishra", age = 25, height = "5'9\"",
            profession = "Student", location = "Lucknow", imageUrl = maleAvatar(23),
            about = "chess, urdu poetry and kebabs",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "22 - 28",
            preferredTopics = "Philosophy, Books, Food & Cooking", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Ex-partners"
        ),
        Profile(
            id = "seed_m_21", name = "Abhinav Das", age = 28, height = "5'11\"",
            profession = "Software Engineer", location = "Bangalore", imageUrl = maleAvatar(24),
            about = "rock climbing on weekends. hit me up if you climb 🧗‍♂️",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Fitness, Travel", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work stress"
        ),
        Profile(
            id = "seed_m_22", name = "Ayush Bose", age = 24, height = "5'10\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = maleAvatar(25),
            about = "writer. usually found at tea stalls reading sci-fi",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Books, Science, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Small talk"
        ),
        Profile(
            id = "seed_m_23", name = "Tushar Agarwal", age = 26, height = "5'9\"",
            profession = "Product / UX Designer", location = "Pune", imageUrl = maleAvatar(26),
            about = "indie rock gigs and spontaneous road trips",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Art & Design, Music, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial talk"
        ),
        Profile(
            id = "seed_m_24", name = "Manav Bhatt", age = 25, height = "6'0\"",
            profession = "Fitness / Wellness", location = "Dehradun", imageUrl = maleAvatar(27),
            about = "me and my golden retriever in the hills 🐕",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 30",
            preferredTopics = "Fitness, Travel, Philosophy", personalityType = "Extrovert",
            conversationIntensity = "Deep", avoidedTopics = "City complaints"
        ),
        Profile(
            id = "seed_m_25", name = "Mohit Hegde", age = 27, height = "5'10\"",
            profession = "Finance / Banking", location = "Bangalore", imageUrl = maleAvatar(0),
            about = "udupi breakfast lover. masala dosa rating expert",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Music, Tech & AI, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Salary, Politics"
        ),
        Profile(
            id = "seed_m_26", name = "Tarun Chopra", age = 29, height = "5'11\"",
            profession = "Entrepreneur / Business", location = "Delhi NCR", imageUrl = maleAvatar(1),
            about = "roasting coffee and watching old movies ☕",
            gender = "Male", lookingFor = "Networking", preferredAgeRange = "26 - 34",
            preferredTopics = "Food & Cooking, Movies & TV, Books", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Status, Gossip"
        ),
        Profile(
            id = "seed_m_27", name = "Vihaan Chawla", age = 24, height = "5'9\"",
            profession = "Student", location = "Chandigarh", imageUrl = maleAvatar(2),
            about = "law student. gully cricket and dogs",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Philosophy, Fitness, Books", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_m_28", name = "Shaurya Trivedi", age = 26, height = "6'1\"",
            profession = "Doctor / Healthcare", location = "Ahmedabad", imageUrl = maleAvatar(3),
            about = "tennis, podcasts and long walks",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Science, Fitness, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital stories"
        ),
        Profile(
            id = "seed_m_29", name = "Mayank Sundaram", age = 28, height = "5'10\"",
            profession = "Software Engineer", location = "Chennai", imageUrl = maleAvatar(4),
            about = "compiler engineer. chess player, terrible at veena",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Music, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk"
        ),
        Profile(
            id = "seed_m_30", name = "Chirag Mukherjee", age = 25, height = "5'8\"",
            profession = "Marketing / Growth", location = "Kolkata", imageUrl = maleAvatar(5),
            about = "park street gigs and street sketching",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Art & Design, Music, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Gossip"
        ),
        Profile(
            id = "seed_m_31", name = "Vivaan Varma", age = 27, height = "5'11\"",
            profession = "Architect", location = "Bangalore", imageUrl = maleAvatar(6),
            about = "cubbon park morning rides and cold brew ☕",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Architecture, Travel, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats"
        ),
        Profile(
            id = "seed_m_32", name = "Samarth Sethi", age = 26, height = "6'0\"",
            profession = "Finance / Banking", location = "Delhi NCR", imageUrl = maleAvatar(7),
            about = "quant researcher. board games and sci-fi movies",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Gaming, Movies & TV, Science", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Stock tips, Money"
        ),
        Profile(
            id = "seed_m_33", name = "Tanmay Nambiar", age = 25, height = "5'10\"",
            profession = "Software Engineer", location = "Kochi", imageUrl = maleAvatar(8),
            about = "software engineer. running by the beach at sunset 🏃‍♂️",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Fitness, Music", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office drama"
        ),
        Profile(
            id = "seed_m_34", name = "Neil Mathur", age = 24, height = "5'9\"",
            profession = "Product / UX Designer", location = "Hyderabad", imageUrl = maleAvatar(9),
            about = "pasta making experiments and vintage cameras",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Art & Design, Food & Cooking, Tech & AI", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Gossip"
        ),
        Profile(
            id = "seed_m_35", name = "Reyansh Bhatia", age = 28, height = "6'1\"",
            profession = "Entrepreneur / Business", location = "Chandigarh", imageUrl = maleAvatar(10),
            about = "gym on weekdays, road trips on sundays",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 33",
            preferredTopics = "Fitness, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Drama, Cynicism"
        ),
        Profile(
            id = "seed_m_36", name = "Anirudh Rawat", age = 26, height = "5'11\"",
            profession = "Teacher / Professor", location = "Dehradun", imageUrl = maleAvatar(11),
            about = "literature, stargazing and quiet evenings",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Philosophy, Music", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Politics"
        ),
        Profile(
            id = "seed_m_37", name = "Agastya Dutta", age = 25, height = "5'8\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = maleAvatar(12),
            about = "tell me your favorite ray film",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Movies & TV, Music, Books", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Past Relationships"
        ),
        Profile(
            id = "seed_m_38", name = "Parth Parekh", age = 27, height = "5'10\"",
            profession = "Lawyer / Legal", location = "Mumbai", imageUrl = maleAvatar(13),
            about = "sailing on weekends, jazz in bandra ⛵",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Books, Travel, Philosophy", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Office Politics"
        ),
        Profile(
            id = "seed_m_39", name = "Advait Goswami", age = 24, height = "5'9\"",
            profession = "Student", location = "Jaipur", imageUrl = maleAvatar(14),
            about = "film photography and old monuments 📸",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Architecture, Art & Design, Travel", personalityType = "Introvert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Academic stress"
        ),
        Profile(
            id = "seed_m_40", name = "Ishan Pandey", age = 26, height = "6'0\"",
            profession = "Doctor / Healthcare", location = "Lucknow", imageUrl = maleAvatar(15),
            about = "neuro resident. chess and street food",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "24 - 31",
            preferredTopics = "Science, Philosophy, Food & Cooking", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital complaints"
        ),
        Profile(
            id = "seed_m_41", name = "Raghav Ghosh", age = 28, height = "5'11\"",
            profession = "Software Engineer", location = "Pune", imageUrl = maleAvatar(16),
            about = "robotics engineer. training for a triathlon 🚴‍♂️",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Tech & AI, Fitness, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work politics"
        ),
        Profile(
            id = "seed_m_42", name = "Shreyas Grover", age = 25, height = "5'10\"",
            profession = "Product / UX Designer", location = "Delhi NCR", imageUrl = maleAvatar(17),
            about = "designer. obsessed with coffee and vintage watches",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "23 - 30",
            preferredTopics = "Art & Design, Psychology, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial banter"
        ),
        Profile(
            id = "seed_m_43", name = "Tejas Shukla", age = 27, height = "5'9\"",
            profession = "Marketing / Growth", location = "Bangalore", imageUrl = maleAvatar(18),
            about = "doing open mics for fun. tell me a good joke",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "24 - 31",
            preferredTopics = "Movies & TV, Music, Travel", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Religion"
        ),
        Profile(
            id = "seed_m_44", name = "Vedant Sood", age = 26, height = "6'1\"",
            profession = "Finance / Banking", location = "Mumbai", imageUrl = maleAvatar(19),
            about = "trekking in the himalayas or eating ramen",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Fitness, Travel, Movies & TV", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Salary talk"
        ),
        Profile(
            id = "seed_m_45", name = "Ojas Somani", age = 24, height = "5'10\"",
            profession = "Student", location = "Ahmedabad", imageUrl = maleAvatar(20),
            about = "mba student surviving on espresso and board games",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Gaming, Tech & AI, Art & Design", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_m_46", name = "Nakul Agnihotri", age = 29, height = "5'11\"",
            profession = "Architect", location = "Bhopal", imageUrl = maleAvatar(21),
            about = "architect. love clay pottery and old folk tunes",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "26 - 34",
            preferredTopics = "Architecture, Science, Philosophy", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chatter"
        ),
        Profile(
            id = "seed_m_47", name = "Ayan Bansal", age = 25, height = "5'9\"",
            profession = "Software Engineer", location = "Bangalore", imageUrl = maleAvatar(22),
            about = "bangalore dev. coffee, code and badminton 🏸",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Office drama"
        ),
        Profile(
            id = "seed_m_48", name = "Samar Sengupta", age = 27, height = "6'0\"",
            profession = "Writer / Content Creator", location = "Kolkata", imageUrl = maleAvatar(23),
            about = "writer. rainy days, books and train journeys",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Books, Philosophy, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Gossip, Small talk"
        ),
        Profile(
            id = "seed_m_49", name = "Hrithik Chauhan", age = 24, height = "5'10\"",
            profession = "Fitness / Wellness", location = "Hyderabad", imageUrl = maleAvatar(24),
            about = "coach. foodie at heart though 🍲",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "22 - 28",
            preferredTopics = "Fitness, Food & Cooking, Music", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Cynicism"
        ),
        Profile(
            id = "seed_m_50", name = "Krrish Nadkarni", age = 28, height = "5'11\"",
            profession = "Doctor / Healthcare", location = "Mumbai", imageUrl = maleAvatar(25),
            about = "surgeon. playing piano when i get a break",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 33",
            preferredTopics = "Music, Science, Travel", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Hospital politics"
        ),
        Profile(
            id = "seed_m_51", name = "Ranveer Kaul", age = 26, height = "6'0\"",
            profession = "Marketing / Growth", location = "Delhi NCR", imageUrl = maleAvatar(26),
            about = "delhi bicycle rides and indie music gigs 🚲",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "24 - 31",
            preferredTopics = "Music, Travel, Food & Cooking", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics"
        ),
        Profile(
            id = "seed_m_52", name = "Shaheer Swaminathan", age = 27, height = "5'10\"",
            profession = "Lawyer / Legal", location = "Chennai", imageUrl = maleAvatar(27),
            about = "tennis, coffee, and good conversation",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "25 - 32",
            preferredTopics = "Philosophy, Books, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Court gossip"
        ),
        Profile(
            id = "seed_m_53", name = "Zayd Ganguly", age = 25, height = "5'9\"",
            profession = "Product / UX Designer", location = "Bangalore", imageUrl = maleAvatar(0),
            about = "street photography and old movies",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Art & Design, Movies & TV, Food & Cooking", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial talk"
        ),
        Profile(
            id = "seed_m_54", name = "Armaan Rajput", age = 28, height = "6'1\"",
            profession = "Entrepreneur / Business", location = "Jaipur", imageUrl = maleAvatar(1),
            about = "craft beer enthusiast and road tripper 🍺",
            gender = "Male", lookingFor = "Networking", preferredAgeRange = "26 - 34",
            preferredTopics = "Food & Cooking, Travel, Tech & AI", personalityType = "Extrovert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Politics, Status"
        ),
        Profile(
            id = "seed_m_55", name = "Farhan Saxena", age = 24, height = "5'10\"",
            profession = "Student", location = "Pune", imageUrl = maleAvatar(2),
            about = "cycling around pune and listening to podcasts",
            gender = "Male", lookingFor = "Casual chat", preferredAgeRange = "22 - 28",
            preferredTopics = "Science, Travel, Fitness", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Exams stress"
        ),
        Profile(
            id = "seed_m_56", name = "Vivian D'Souza", age = 27, height = "5'11\"",
            profession = "Teacher / Professor", location = "Goa", imageUrl = maleAvatar(3),
            about = "music teacher in goa. sunset walks and guitar 🎸",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 32",
            preferredTopics = "Music, Philosophy, Travel", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "City rush complaints"
        ),
        Profile(
            id = "seed_m_57", name = "Alok Sen", age = 26, height = "5'9\"",
            profession = "Finance / Banking", location = "Kolkata", imageUrl = maleAvatar(4),
            about = "theatre, chess, and old bookstores",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Books, Art & Design, Movies & TV", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Salary talk"
        ),
        Profile(
            id = "seed_m_58", name = "Madhav Sharma", age = 25, height = "5'10\"",
            profession = "Software Engineer", location = "Hyderabad", imageUrl = maleAvatar(5),
            about = "cybersecurity. badminton and spicy biryani 🏸",
            gender = "Male", lookingFor = "Friendship", preferredAgeRange = "23 - 29",
            preferredTopics = "Tech & AI, Gaming, Food & Cooking", personalityType = "Ambivert",
            conversationIntensity = "Lighthearted", avoidedTopics = "Work politics"
        ),
        Profile(
            id = "seed_m_59", name = "Kabir Deshmukh", age = 28, height = "6'0\"",
            profession = "Architect", location = "Mumbai", imageUrl = maleAvatar(6),
            about = "marine drive breeze and black coffee ☕",
            gender = "Male", lookingFor = "Meaningful conversations", preferredAgeRange = "25 - 33",
            preferredTopics = "Architecture, Art & Design, Travel", personalityType = "Introvert",
            conversationIntensity = "Deep", avoidedTopics = "Small talk, Gossip"
        ),
        Profile(
            id = "seed_m_60", name = "Rishi Mehra", age = 26, height = "5'11\"",
            profession = "Product / UX Designer", location = "Bangalore", imageUrl = maleAvatar(7),
            about = "designer in blr. vinyl listener and espresso tinkerer",
            gender = "Male", lookingFor = "Dating", preferredAgeRange = "24 - 31",
            preferredTopics = "Art & Design, Music, Books", personalityType = "Ambivert",
            conversationIntensity = "Deep", avoidedTopics = "Superficial chats, Office Drama"
        )
    )

    fun getSeedProfiles(userGender: String?, shuffled: Boolean = false): List<Profile> {
        val list = when {
            userGender.equals("Female", ignoreCase = true) -> MALE_PROFILES
            userGender.equals("Male", ignoreCase = true) -> FEMALE_PROFILES
            else -> FEMALE_PROFILES
        }
        return if (shuffled) list.shuffled() else list
    }

    fun getProfileById(id: String): Profile? =
        (FEMALE_PROFILES + MALE_PROFILES).firstOrNull { it.id == id }

    fun isSeedProfile(id: String?): Boolean =
        id?.startsWith("seed_") == true
}
