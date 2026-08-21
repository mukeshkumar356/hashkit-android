package com.hashkit.app;

import java.util.*;

public class HashtagData {

    // Category → 30 hashtags (first 5 are trending/top)
    private static final Map<String, String[]> DATA = new LinkedHashMap<>();

    static {
        DATA.put("food", new String[]{
            "#foodie","#instafood","#foodphotography","#yummy","#delicious",
            "#homemadewithlove","#foodblogger","#foodlovers","#tasty","#healthyfood",
            "#cooking","#recipe","#dinner","#lunch","#breakfast",
            "#foodstagram","#cheflife","#kitchenlife","#eatwell","#foodart",
            "#mealprep","#freshfood","#foodgasm","#foodporn","#nom",
            "#feedfeed","#eeeeeats","#forkyeah","#huffposttaste","#buzzfeedfood"
        });
        DATA.put("travel", new String[]{
            "#travel","#travelgram","#wanderlust","#explore","#adventure",
            "#travelblogger","#instatravel","#travelphoto","#globetrotter","#vacation",
            "#nature","#landscape","#photography","#travelphotography","#backpacker",
            "#roadtrip","#mountains","#beach","#sunsets","#cityscape",
            "#traveler","#nomad","#exploremore","#worldtravel","#seetheworld",
            "#traveladdict","#passionpassport","#lonelyplanet","#beautifuldestinations","#earthpix"
        });
        DATA.put("fashion", new String[]{
            "#fashion","#style","#ootd","#outfitoftheday","#fashionista",
            "#fashionblogger","#streetstyle","#instafashion","#lookoftheday","#whatiwore",
            "#clothes","#shopping","#trending","#stylish","#outfit",
            "#menstyle","#womensfashion","#model","#photooftheday","#selfie",
            "#luxury","#brands","#designer","#vogue","#glam",
            "#chic","#slay","#dressed","#fashionweek","#styleinspo"
        });
        DATA.put("fitness", new String[]{
            "#fitness","#gym","#workout","#fitnessmotivation","#fit",
            "#gymlife","#bodybuilding","#health","#training","#exercise",
            "#fitfam","#healthy","#sport","#motivation","#gains",
            "#cardio","#weightloss","#abs","#strength","#athlete",
            "#lifestyle","#nopainnogain","#pushyourself","#personaltrainer","#fitnessjourney",
            "#wellness","#yogalife","#running","#crossfit","#nutrition"
        });
        DATA.put("photography", new String[]{
            "#photography","#photo","#photooftheday","#photographer","#instagram",
            "#picoftheday","#portrait","#naturephotography","#streetphotography","#travelphotography",
            "#nikon","#canon","#sigma","#landscape","#lightroom",
            "#shutterbug","#capture","#raw","#golden hour","#composition",
            "#colorphotography","#blackandwhite","#macro","#editoftheday","#vsco",
            "#photographylovers","#natgeo","#500px","#artofvisuals","#agameoftones"
        });
        DATA.put("motivation", new String[]{
            "#motivation","#inspiration","#success","#mindset","#hustle",
            "#goals","#positivevibes","#entrepreneur","#dream","#grind",
            "#nevergiveup","#hardwork","#believe","#focused","#winner",
            "#selfimprovement","#growth","#dailymotivation","#abundance","#manifest",
            "#levelup","#bossmoves","#buildinganempire","#ambition","#dedication",
            "#motivationalquotes","#inspirationalquotes","#quoteoftheday","#lifequotes","#successmindset"
        });
        DATA.put("beauty", new String[]{
            "#beauty","#makeup","#skincare","#glam","#makeuplover",
            "#beautyblogger","#makeuptutorial","#lipstick","#eyeshadow","#contour",
            "#naturalmakeup","#skincareroutine","#glowing","#selfcare","#gorgeous",
            "#beautytips","#makeupoftheday","#cosmetics","#highlighter","#foundation",
            "#makeupartist","#mua","#beautiful","#instamakeup","#instabeauty",
            "#ulta","#sephora","#drugstorebeauty","#cleanbeauty","#makeupjunkie"
        });
        DATA.put("business", new String[]{
            "#entrepreneur","#business","#success","#startup","#money",
            "#digitalmarketing","#marketing","#socialmedia","#branding","#smallbusiness",
            "#businessowner","#ceo","#investing","#finance","#wealth",
            "#passiveincome","#sidehustle","#onlinebusiness","#ecommerce","#dropshipping",
            "#leadership","#mindset","#sales","#hustler","#networking",
            "#businesstips","#growthhacking","#smm","#contentmarketing","#b2b"
        });
        DATA.put("nature", new String[]{
            "#nature","#naturephotography","#wildlife","#forest","#mountains",
            "#sky","#sunset","#flowers","#green","#earth",
            "#outdoors","#hiking","#camping","#trees","#waterfall",
            "#oceanview","#wildlife","#birding","#macro","#seasons",
            "#naturalbeauty","#earthpix","#natgeo","#wildlifephotography","#planetearth",
            "#landscapephotography","#cloudporn","#skylovers","#sunsetphotography","#igtravel"
        });
        DATA.put("reels", new String[]{
            "#reels","#reelsinstagram","#reelsvideo","#reelsindia","#viralreels",
            "#trending","#viral","#fyp","#explore","#trendingnow",
            "#reelitfeelit","#reelkarofeelkaro","#instareels","#shortsvideo","#content",
            "#creator","#contentcreator","#influencer","#instadaily","#instagood",
            "#viralvideo","#trendingvideo","#comedy","#dance","#entertainment",
            "#funny","#memes","#mood","#lifestyle","#daily"
        });
        DATA.put("lifestyle", new String[]{
            "#lifestyle","#life","#happy","#instagood","#love",
            "#photooftheday","#instadaily","#friends","#fun","#smile",
            "#positivevibes","#goodvibes","#blessed","#grateful","#mindfulness",
            "#livingmybestlife","#selfcare","#wellness","#balance","#peace",
            "#homebody","#cozy","#daily","#vibes","#aesthetics",
            "#morningroutine","#sundays","#weekendvibes","#homestyle","#interiordesign"
        });
        DATA.put("tech", new String[]{
            "#tech","#technology","#ai","#programming","#coding",
            "#developer","#software","#innovation","#startup","#digital",
            "#machinelearning","#artificialintelligence","#python","#javascript","#android",
            "#ios","#gadgets","#smartphone","#laptop","#cybersecurity",
            "#blockchain","#web3","#crypto","#iot","#cloud",
            "#devops","#openai","#chatgpt","#datascience","#techindustry"
        });
        DATA.put("india", new String[]{
            "#india","#incredible_india","#indianphotography","#desi","#bharat",
            "#indiapictures","#indiaclicks","#ig_india","#indiagram","#indianstreetphotography",
            "#delhi","#mumbai","#bangalore","#jaipur","#kerala",
            "#indianfood","#indianculture","#indianwedding","#indiatravel","#travelindia",
            "#delhigram","#mumbaidiaries","#bangaloregram","#exploreindia","#godsowncountry",
            "#reelsindia","#indianinfluencer","#madeinindia","#vocal4local","#atithi"
        });
        DATA.put("art", new String[]{
            "#art","#artwork","#artist","#drawing","#painting",
            "#illustration","#sketch","#digitalart","#creative","#design",
            "#artofinstagram","#contemporaryart","#abstractart","#watercolor","#oilpainting",
            "#handmade","#crafts","#diy","#artgallery","#instaart",
            "#artlovers","#supportart","#artistsoninstagram","#artstagram","#artlife",
            "#procreate","#graphicdesign","#typography","#logo","#branding"
        });
        DATA.put("music", new String[]{
            "#music","#musician","#singer","#song","#guitar",
            "#piano","#hiphop","#rap","#rnb","#pop",
            "#indiemusic","#newmusic","#musicproducer","#studio","#recording",
            "#beatmaker","#producer","#dj","#concert","#livemusic",
            "#musiclovers","#playlist","#spotify","#lyrics","#vibes",
            "#bollywood","#newsong","#musicvideo","#indiepop","#underground"
        });
    }

    public static String[] getHashtags(String category) {
        String key = category.toLowerCase().trim();
        if (DATA.containsKey(key)) return DATA.get(key);

        // Fuzzy match
        for (String k : DATA.keySet()) {
            if (k.contains(key) || key.contains(k)) return DATA.get(k);
        }
        return generateFromKeyword(category);
    }

    private static String[] generateFromKeyword(String keyword) {
        String kw = keyword.toLowerCase().replaceAll("\\s+", "").replaceAll("[^a-z0-9]", "");
        String kwSpace = keyword.toLowerCase().replaceAll("[^a-z0-9 ]", "").replaceAll("\\s+", "");

        List<String> tags = new ArrayList<>();
        // Direct keyword tags
        tags.add("#" + kw);
        tags.add("#" + kw + "photography");
        tags.add("#" + kw + "lover");
        tags.add("#" + kw + "life");
        tags.add("#" + kw + "gram");
        tags.add("#" + kw + "daily");
        tags.add("#" + kw + "oftheday");
        tags.add("#" + kw + "art");
        tags.add("#best" + kw);
        tags.add("#" + kw + "world");
        tags.add("#" + kw + "community");
        tags.add("#" + kw + "hub");
        tags.add("#" + kw + "fans");
        tags.add("#" + kw + "love");
        tags.add("#" + kw + "vibes");
        // Generic trending tags
        String[] generic = {"#instagood","#photooftheday","#trending","#viral","#explore",
            "#love","#instagram","#photography","#beautiful","#happy",
            "#follow","#picoftheday","#like4like","#instalike","#instadaily"};
        tags.addAll(Arrays.asList(generic));
        return tags.toArray(new String[0]);
    }

    public static List<String> getCategories() {
        return new ArrayList<>(DATA.keySet());
    }

    // Map ML Kit labels to our categories
    public static String labelToCategory(String label) {
        label = label.toLowerCase();
        if (label.contains("food") || label.contains("dish") || label.contains("cuisine")
                || label.contains("fruit") || label.contains("vegetable") || label.contains("meal")
                || label.contains("drink") || label.contains("bakery") || label.contains("pizza")
                || label.contains("burger") || label.contains("dessert") || label.contains("coffee"))
            return "food";
        if (label.contains("fashion") || label.contains("cloth") || label.contains("dress")
                || label.contains("outfit") || label.contains("wear") || label.contains("shirt")
                || label.contains("jeans") || label.contains("shoe") || label.contains("bag"))
            return "fashion";
        if (label.contains("fitness") || label.contains("gym") || label.contains("sport")
                || label.contains("exercise") || label.contains("athlete") || label.contains("yoga")
                || label.contains("running") || label.contains("muscle"))
            return "fitness";
        if (label.contains("sky") || label.contains("mountain") || label.contains("forest")
                || label.contains("tree") || label.contains("flower") || label.contains("plant")
                || label.contains("ocean") || label.contains("river") || label.contains("lake")
                || label.contains("grass") || label.contains("landscape") || label.contains("sunset"))
            return "nature";
        if (label.contains("travel") || label.contains("city") || label.contains("building")
                || label.contains("architecture") || label.contains("street") || label.contains("road")
                || label.contains("vehicle") || label.contains("airplane") || label.contains("hotel"))
            return "travel";
        if (label.contains("art") || label.contains("paint") || label.contains("drawing")
                || label.contains("sculpture") || label.contains("design") || label.contains("illustration"))
            return "art";
        if (label.contains("music") || label.contains("guitar") || label.contains("piano")
                || label.contains("instrument") || label.contains("concert") || label.contains("singer"))
            return "music";
        if (label.contains("tech") || label.contains("computer") || label.contains("phone")
                || label.contains("laptop") || label.contains("device") || label.contains("screen"))
            return "tech";
        if (label.contains("selfie") || label.contains("person") || label.contains("human")
                || label.contains("face") || label.contains("portrait"))
            return "lifestyle";
        return "lifestyle";
    }

    // Captions per category
    private static final Map<String, String[]> CAPTIONS = new LinkedHashMap<>();
    static {
        CAPTIONS.put("food", new String[]{
            "Good food is the foundation of genuine happiness 🍽️✨\nEat what makes your soul smile 😋",
            "Life is short, eat the dessert first 🍰💫\nNo regrets, only delicious memories!",
            "Cooking is love made visible 💕🍳\nEvery bite tells a story worth savoring",
            "You can't live a full life on an empty stomach 🌮🔥\nFueling dreams one meal at a time",
            "First, we eat. Then we do everything else 🍜⭐\nFood is my love language!",
            "The secret ingredient is always love ❤️🧑‍🍳\nMade with heart, served with soul",
            "Good vibes happen on the tides of great food 🌊🍕\nHappy tummy, happy life!",
            "Eating is a necessity, but cooking is an art 🎨🥘\nMasterpiece in progress!",
            "Food brings people together like nothing else 🤝🍱\nShared plates, shared memories",
            "Life is better with good food and great company 🥂🎉\nCheers to every delicious moment!"
        });
        CAPTIONS.put("travel", new String[]{
            "Not all those who wander are lost ✈️🌍\nSome are just discovering the world",
            "Travel far enough, you meet yourself 🗺️💫\nEvery journey begins with a single step",
            "Collect moments, not things 📸🌅\nMemories are the best souvenirs",
            "Adventure awaits — go find it! 🏔️🌊\nThe world is too beautiful to stay in one place",
            "Wanderlust: a strong desire to travel 🧳✨\nFeed your soul with new experiences",
            "Jobs fill your pocket, adventure fills your soul 💼🔥\nChoose experiences over things",
            "Life is a journey, not a destination 🛤️🌟\nEnjoy every beautiful mile",
            "Wherever you go, go with all your heart 💙✈️\nTravel is the only thing you buy that makes you richer",
            "The world is a book, those who don't travel read only one page 📖🌐\nKeep turning the pages!",
            "Take only memories, leave only footprints 👣🌿\nTravel light, dream heavy"
        });
        CAPTIONS.put("fashion", new String[]{
            "Style is a way to say who you are without speaking 👗✨\nLet your outfit do the talking",
            "Fashion is art and you are the canvas 🎨💃\nWear your confidence like a crown",
            "Dress how you want to be addressed 👠🔥\nYour style, your rules",
            "Life is too short to wear boring clothes 🌈💫\nBe bold, be you, be unforgettable",
            "Fashion fades, style is eternal 💎✨\nInvest in pieces that speak your truth",
            "Confidence is the best outfit — rock it and own it 💪👑\nSlaying since day one",
            "I don't do fashion, I am fashion 🌟👒\nTrending on my own terms",
            "Good clothes open all doors 🚪✨\nDress well, live better",
            "Style is knowing who you are and having the courage to own it 💕🌟\nFlawless and unapologetic",
            "Fashion is what you buy, style is what you do with it 🛍️✨\nCreate your own trend"
        });
        CAPTIONS.put("fitness", new String[]{
            "Sweat now, shine later 💪🔥\nThe grind doesn't stop until the goal is reached",
            "Your body can stand almost anything — it's your mind you have to convince 🧠⚡\nPush harder than yesterday",
            "Train insane or remain the same 🏋️‍♀️💯\nNo shortcuts to any place worth going",
            "Fitness is not about being better than someone else 🥇✨\nIt's about being better than you used to be",
            "The only bad workout is the one that didn't happen 🏃‍♂️🌟\nShow up, every single day",
            "Strong is the new beautiful 💪🌸\nBuilding the best version of myself",
            "It always seems impossible until it's done 🎯🔥\nBreak through every limit",
            "Fall in love with taking care of yourself 🌿💚\nMind, body, and soul aligned",
            "Progress over perfection, every single time 📈⚡\nSmall steps lead to big changes",
            "Earn your body 🏆💪\nDiscipline is doing it even when you don't feel like it"
        });
        CAPTIONS.put("motivation", new String[]{
            "Dream big, work hard, stay humble 🌟💼\nSuccess is built one disciplined day at a time",
            "Your only limit is your mind 🧠🚀\nBreak the cage, unleash the beast within",
            "Success doesn't come to you, you go to it 🏆🔥\nStop wishing, start doing",
            "The best investment you can make is in yourself 💡📈\nGrow every single day",
            "Don't watch the clock — do what it does. Keep going ⏰💪\nTime waits for no one",
            "Believe in yourself so hard that doubt has no room to breathe 💯✨\nYou are capable of greatness",
            "One day or day one — you decide 📅🔥\nThe future belongs to those who start today",
            "Difficult roads often lead to beautiful destinations 🛣️🌅\nTrust the journey",
            "Be the energy you want to attract 🌊⚡\nYour vibe creates your reality",
            "Chase the vision, not the money 👁️💫\nMoney follows passion, not the other way around"
        });
        CAPTIONS.put("beauty", new String[]{
            "Beauty begins the moment you decide to be yourself 💄✨\nAuthentic is the new gorgeous",
            "Life is short, buy the lipstick 💋🛍️\nGlow different, glow bright",
            "Makeup is my armor, confidence is my weapon 💪💄\nFace the world flawlessly",
            "Glowing inside and out 🌟💛\nSkin care is self care, never skip it",
            "You are beautiful — and don't you forget it 💕✨\nNo filter needed for a genuine smile",
            "Wake up, makeup, slay all day 💅🔥\nBoss babe energy loading...",
            "Pretty hurts? Nah, pretty empowers 👑💄\nOwn your glow unapologetically",
            "Good skin is always in 🌸💎\nInvest in your skin — it's going to represent you for a long time",
            "Bold lips, wild heart 💋❤️\nBe your own kind of beautiful every day",
            "Elegance is the only beauty that never fades 💫👸\nTimeless, effortless, iconic"
        });
        CAPTIONS.put("nature", new String[]{
            "In every walk with nature, one receives far more than they seek 🌿🌅\nNature never goes out of style",
            "Look deep into nature, and then you will understand everything 🍃🌍\nEarth speaks to those who listen",
            "The Earth has music for those who listen 🎶🌲\nFind your peace in the wild",
            "Nature is not a place to visit — it's home 🏡🌿\nGo outside and breathe",
            "Adopt the pace of nature: her secret is patience 🌸⏳\nSlow down and look around",
            "To walk in nature is to witness a thousand miracles 🌈✨\nOpen your eyes, it's magical",
            "The clearest way into the Universe is through a forest 🌲⭐\nLose yourself, find everything",
            "Take nothing but photos, leave nothing but footprints 📸👣\nProtect what you love",
            "Sunsets are proof that endings can be beautiful 🌅💛\nEvery day deserves a moment of awe",
            "In the middle of difficulty lies opportunity 🌻🔥\nGrow through what you go through"
        });
        CAPTIONS.put("lifestyle", new String[]{
            "Create a life you love, then love the life you created 💕🌟\nDesign your own destiny",
            "Living my best life, one day at a time 🌈✨\nChoose joy, choose growth, choose you",
            "Good vibes only — no room for anything less 🌊💫\nProtect your energy fiercely",
            "Do more of what makes your soul happy 🎉💛\nHappiness is a lifestyle choice",
            "Chasing dreams and good coffee ☕✨\nBusy being happy and productive",
            "Life is what happens between Instagram posts 😂💕\nMake real moments count too",
            "The good life is when you stop wanting a better one 🌸🙏\nGratitude unlocks abundance",
            "Simplicity is the ultimate sophistication 🎯✨\nLess noise, more peace",
            "Surround yourself with people who push you higher 🚀💙\nYour circle is your vibe",
            "Be yourself — everyone else is already taken 🌟😊\nOriginal and proud of it"
        });
        CAPTIONS.put("business", new String[]{
            "Work hard in silence, let success make the noise 🔇🏆\nThe results speak louder than words",
            "Entrepreneurship is living a few years like most won't — so you can live the rest like most can't 🔥💼\nDelayed gratification is the key",
            "Build the business you wish existed 🌐💡\nSolve problems, create value, change lives",
            "Your network is your net worth 🤝📈\nInvest in relationships, not just revenue",
            "The secret of getting ahead is getting started 🚀⚡\nPerfect is the enemy of launched",
            "Don't find customers for your products — find products for your customers 💎🎯\nAlways customer first",
            "Success is not final, failure is not fatal 💪🔄\nIt's the courage to continue that counts",
            "Hustle beats talent when talent doesn't hustle 💯🔥\nOutwork everyone in the room",
            "Create. Inspire. Disrupt. Repeat. ♻️⚡\nThe market rewards those who never stop innovating",
            "Your brand is a story unfolding across all customer touch points 📖💼\nMake every chapter count"
        });
    }

    public static String[] getCaptions(String category) {
        String key = category.toLowerCase().trim();
        if (CAPTIONS.containsKey(key)) return CAPTIONS.get(key);
        for (String k : CAPTIONS.keySet()) {
            if (k.contains(key) || key.contains(k)) return CAPTIONS.get(k);
        }
        return CAPTIONS.get("lifestyle");
    }

    public static List<String> getCaptionCategories() {
        return new ArrayList<>(CAPTIONS.keySet());
    }
}
