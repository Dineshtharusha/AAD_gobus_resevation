/**
 * GoBus Sri Lanka - Localization (i18n) Engine
 * Supported Languages:
 *   - en: English (Default)
 *   - ta: தமிழ் (Tamil)
 *   - si: සිංහල (Sinhala)
 */

(function () {
    'use strict';

    const STORAGE_KEY = 'gobus_lang';
    const DEFAULT_LANG = 'en';

    const translations = {
        en: {
            // Navigation
            'nav.book': 'Book a ticket',
            'nav.offers': 'Offers',
            'nav.howItWorks': 'How it works',
            'nav.contact': 'Contact & Support',
            'nav.login': 'Log in',
            'nav.dashboard': 'My Dashboard',
            'nav.logout': 'Sign out',

            // Hero
            'hero.eyebrow': "Sri Lanka's smarter bus booking platform",
            'hero.title_part1': 'Go further.',
            'hero.title_part2': 'Feel at home.',
            'hero.text': 'Find your next journey in seconds, choose your exact seat, and travel with confidence across Sri Lanka.',
            'hero.tab.oneWay': 'One way',
            'hero.tab.roundTrip': 'Round trip',
            'hero.from': 'From',
            'hero.to': 'To',
            'hero.date': 'Travel date',
            'hero.passengers': 'Passengers',
            'hero.pax.1': '1 passenger',
            'hero.pax.2': '2 passengers',
            'hero.pax.3': '3 passengers',
            'hero.pax.4': '4 passengers',
            'hero.searchBtn': 'Search buses',
            'hero.secureNote': 'Secure booking · Instant e-ticket · Live seat availability',
            'hero.art.tag': 'DISCOVER<br>THE ISLAND',
            'hero.art.route': 'COLOMBO ➔ KANDY',

            // Cities
            'city.colombo': 'Colombo',
            'city.kandy': 'Kandy',
            'city.jaffna': 'Jaffna',
            'city.galle': 'Galle',
            'city.badulla': 'Badulla',

            // Results Section
            'results.eyebrow': 'Available journeys',
            'results.title': 'Choose your ride',
            'results.edit': 'Edit search',
            'results.today': 'Today',
            'results.busesFound': 'buses found',
            'results.seatsLeft': 'seats left',
            'results.fromPrice': 'from',
            'results.chooseSeats': 'Choose seats',
            'results.soldOut': 'Sold out',
            'results.noBuses': 'No buses found',
            'results.noBusesMsg': 'No scheduled trips from {source} to {destination} on this date.',
            'results.noBusesHint': 'Try another date or check available routes below.',

            // Offers Section
            'offers.eyebrow': 'Go somewhere new',
            'offers.title': 'Offers for your next trip',
            'offers.viewAll': 'View all offers',
            'offers.tag.limited': 'LIMITED TIME',
            'offers.card1.title': 'Weekend escape<br>to Ella',
            'offers.card1.text': 'Save up to 20% on selected routes',
            'offers.tag.goFurther': 'GO FURTHER',
            'offers.card2.title': 'Colombo to<br>Jaffna',
            'offers.card2.text': 'Comfort all the way north',
            'offers.tag.new': 'NEW ON GOBUS',
            'offers.card3.title': 'Late night<br>connections',
            'offers.card3.text': 'Arrive fresh for less',

            // Why section
            'why.eyebrow': 'Travel, reimagined',
            'why.title': 'Everything you need for a smoother journey.',
            'why.desc': 'From your first search to the moment you arrive, GoBus keeps the details simple and the journey yours.',
            'why.btn': 'See how it works',
            'why.benefit1.title': 'Your seat, your choice',
            'why.benefit1.desc': 'See live availability and pick the seat that feels right.',
            'why.benefit2.title': 'Your ticket, always with you',
            'why.benefit2.desc': 'Instant e-tickets sent straight to your phone and inbox.',
            'why.benefit3.title': 'Know before you go',
            'why.benefit3.desc': 'Reliable times, clear stops, and helpful trip updates.',

            // Popular Routes
            'popular.eyebrow': 'A little inspiration',
            'popular.title': 'Popular routes',
            'popular.explore': 'Explore all routes',
            'popular.fromPrefix': 'From Rs.',

            // How It Works Steps
            'steps.eyebrow': 'Simple by design',
            'steps.title': 'From search to seat in four steps.',
            'steps.step1.title': 'Search',
            'steps.step1.desc': 'Tell us where you are going and when.',
            'steps.step2.title': 'Choose',
            'steps.step2.desc': 'Compare buses and pick your favourite seat.',
            'steps.step3.title': 'Pay',
            'steps.step3.desc': 'Complete your booking securely online.',
            'steps.step4.title': 'Go',
            'steps.step4.desc': 'Show your e-ticket and enjoy the ride.',

            // Contact & 24/7 Support
            'contact.eyebrow': '24/7 Islandwide Assistance',
            'contact.title': "We're here to help you travel smoothly",
            'contact.desc': 'Have questions regarding bus schedules, seat reservations, cancellations, or lost baggage? Reach out to our dedicated passenger care team anytime.',
            'contact.phone.title': 'Phone & WhatsApp Hotline',
            'contact.phone.text': 'Available 24 hours a day, 7 days a week for immediate boarding assistance, reservation queries, and ticket cancellations.',
            'contact.phone.badge': 'Average response time < 2 mins',
            'contact.terminal.title': 'Terminal Counters',
            'contact.terminal.text': 'Direct assistance on-site at major intercity bus terminals across the island:',
            'contact.term.colombo': 'Bastian Mawatha Terminal (Counter 4)',
            'contact.term.kandy': 'Goods Shed Intercity Stand (Bay 2)',
            'contact.term.galle': 'Galle Central Bus Station (Counter 1)',
            'contact.term.jaffna': 'Jaffna Central Express Desk',
            'contact.form.title': 'Send us a Message',
            'contact.form.subtitle': 'Fill out the form below and an agent will follow up right away.',
            'contact.form.name': 'Full Name *',
            'contact.form.namePlaceholder': 'e.g. Dinesh Perera',
            'contact.form.email': 'Email Address *',
            'contact.form.emailPlaceholder': 'you@example.com',
            'contact.form.phone': 'Phone Number *',
            'contact.form.phonePlaceholder': '+94 77 123 4567',
            'contact.form.subject': 'How can we help you? *',
            'contact.form.selectSubject': 'Select an inquiry topic',
            'contact.form.subBooking': 'Ticket Booking & Seat Availability',
            'contact.form.subCancel': 'Cancellation & Refund Request',
            'contact.form.subLost': 'Lost & Found Baggage',
            'contact.form.subSched': 'Departure Timetable & Delays',
            'contact.form.subOperator': 'Bus Operator & Fleet Registration',
            'contact.form.subOther': 'General Feedback & Other',
            'contact.form.message': 'Your Message *',
            'contact.form.msgPlaceholder': 'Tell us how we can assist you...',
            'contact.form.submit': 'Send Inquiry',

            // Footer
            'footer.tagline': 'More than a ticket.<br>A better way to move.',
            'footer.links.offers': 'Offers',
            'footer.links.how': 'How it works',
            'footer.links.help': 'Help centre',
            'footer.links.contact': 'Contact us',
            'footer.copy': '© 2026 GoBus Sri Lanka',
            'footer.secure': 'Secure payments · Built for every journey',
            'footer.terms': 'Privacy · Terms',

            // Seat Modal
            'modal.seat.eyebrow': 'Select your seat',
            'modal.seat.driver': 'DRIVER',
            'modal.seat.available': 'Available',
            'modal.seat.selected': 'Selected',
            'modal.seat.occupied': 'Occupied',
            'modal.seat.selectedLabel': 'Selected seat',
            'modal.seat.choosePrompt': 'Choose a seat',
            'modal.seat.seatPrefix': 'Seat',
            'modal.seat.continue': 'Continue',

            // Chatbot
            'chatbot.title': 'GoBus Travel Assistant',
            'chatbot.status': '24/7 AI-powered • Always online',
            'chatbot.placeholder': 'Ask about routes, schedules, fares, bookings…',
            'chatbot.footer': 'Powered by GoBus AI',

            // Auth
            'auth.welcomeBack': 'Welcome back',
            'auth.username': 'Username or Email',
            'auth.usernamePlaceholder': 'Enter your username or email',
            'auth.password': 'Password',
            'auth.passwordPlaceholder': 'Enter your password',
            'auth.remember': 'Remember me',
            'auth.forgot': 'Forgot password?',
            'auth.signIn': 'Sign in',
            'auth.noAccount': "Don't have an account?",
            'auth.signUp': 'Sign up'
        },

        ta: {
            // Navigation
            'nav.book': 'டிக்கெட் பதிவு',
            'nav.offers': 'சலுகைகள்',
            'nav.howItWorks': 'செயல்படும் விதம்',
            'nav.contact': 'தொடர்பு & உதவி',
            'nav.login': 'உள்நுழைக',
            'nav.dashboard': 'எனது கணக்கு',
            'nav.logout': 'வெளியேறுக',

            // Hero
            'hero.eyebrow': 'இலங்கையின் சிறந்த பேருந்து முன்பதிவு தளம்',
            'hero.title_part1': 'தொலைதூர பயணம்.',
            'hero.title_part2': 'சொந்த ஊர் உணர்வு.',
            'hero.text': 'நொடிகளில் உங்கள் பயணத்தைத் தேடுங்கள், உங்கள் இருக்கையைத் தேர்ந்தெடுத்து நம்பிக்கையுடன் பயணியுங்கள்.',
            'hero.tab.oneWay': 'ஒரு வழி',
            'hero.tab.roundTrip': 'இரு வழி',
            'hero.from': 'புறப்படும் இடம்',
            'hero.to': 'சேருமிடம்',
            'hero.date': 'பயண தேதி',
            'hero.passengers': 'பயணிகள்',
            'hero.pax.1': '1 பயணி',
            'hero.pax.2': '2 பயணிகள்',
            'hero.pax.3': '3 பயணிகள்',
            'hero.pax.4': '4 பயணிகள்',
            'hero.searchBtn': 'பேருந்துகளைத் தேடுக',
            'hero.secureNote': 'பாதுகாப்பான முன்பதிவு · உடனடி மின்-டிக்கெட் · நேரலை இருக்கை விவரம்',
            'hero.art.tag': 'தீவை<br>கண்டறியுங்கள்',
            'hero.art.route': 'கொழும்பு ➔ கண்டி',

            // Cities
            'city.colombo': 'கொழும்பு',
            'city.kandy': 'கண்டி',
            'city.jaffna': 'யாழ்ப்பாணம்',
            'city.galle': 'காலி',
            'city.badulla': 'பதுளை',

            // Results Section
            'results.eyebrow': 'கிடைக்கும் பயணங்கள்',
            'results.title': 'உங்கள் பயணத்தைத் தேர்ந்தெடுக்கவும்',
            'results.edit': 'தேடலை மாற்றுக',
            'results.today': 'இன்று',
            'results.busesFound': 'பேருந்துகள் கிடைத்தன',
            'results.seatsLeft': 'இருக்கைகள் உள்ளன',
            'results.fromPrice': 'ஆரம்ப விலை',
            'results.chooseSeats': 'இருக்கையைத் தேர்வுசெய்க',
            'results.soldOut': 'முடிந்துவிட்டது',
            'results.noBuses': 'பேருந்துகள் இல்லை',
            'results.noBusesMsg': '{source} இலிருந்து {destination} க்கு இந்த தேதியில் பேருந்துகள் இல்லை.',
            'results.noBusesHint': 'மற்றொரு தேதியைத் தேர்ந்தெடுக்கவும் அல்லது கீழே உள்ள வழிகளைப் பார்க்கவும்.',

            // Offers Section
            'offers.eyebrow': 'புதிய இடங்களுக்குப் பயணம்',
            'offers.title': 'உங்கள் அடுத்த பயணத்திற்கான சலுகைகள்',
            'offers.viewAll': 'அனைத்து சலுகைகளையும் காண்க',
            'offers.tag.limited': 'குறிப்பிட்ட காலம் மட்டுமே',
            'offers.card1.title': 'எல்லாவிற்கு வார<br>இறுதி பயணம்',
            'offers.card1.text': 'தேர்ந்தெடுக்கப்பட்ட வழிகளில் 20% வரை தள்ளுபடி',
            'offers.tag.goFurther': 'நீண்ட தூர பயணம்',
            'offers.card2.title': 'கொழும்பு முதல்<br>யாழ்ப்பாணம்',
            'offers.card2.text': 'வடக்கு நோக்கிய சொகுசு பயணம்',
            'offers.tag.new': 'கோபஸில் புதியது',
            'offers.card3.title': 'இரவு நேரப்<br>பயணங்கள்',
            'offers.card3.text': 'குறைந்த கட்டணத்தில் வசதியான பயணம்',

            // Why section
            'why.eyebrow': 'பயணம், புதிய வடிவில்',
            'why.title': 'இனிமையான பயணத்திற்கு தேவையான அனைத்தும்.',
            'why.desc': 'முதல் தேடலில் இருந்து நீங்கள் சென்றடையும் வரை, உங்கள் பயணத்தை எளிமையாகவும் பாதுகாப்பாகவும் ஆக்குகிறோம்.',
            'why.btn': 'செயல்பாட்டை காண்க',
            'why.benefit1.title': 'உங்கள் இருக்கை, உங்கள் விருப்பம்',
            'why.benefit1.desc': 'நேரலை இருக்கை நிலவரத்தைப் பார்த்து உங்களுக்குப் பிடித்த இருக்கையைத் தேர்வுசெய்யுங்கள்.',
            'why.benefit2.title': 'உங்கள் டிக்கெட், எப்போதும் உங்களுடன்',
            'why.benefit2.desc': 'உடனடி மின்-டிக்கெட் உங்கள் தொலைபேசி மற்றும் மின்னஞ்சலுக்கு உடனடியாக அனுப்பப்படும்.',
            'why.benefit3.title': 'பயணத்திற்கு முன் அறிந்து கொள்ளுங்கள்',
            'why.benefit3.desc': 'துல்லியமான புறப்படும் நேரம் மற்றும் பயண தகவல்கள் உடனுக்குடன் வழங்கப்படும்.',

            // Popular Routes
            'popular.eyebrow': 'பிரபலமான வழிகள்',
            'popular.title': 'அடிக்கடி பயணிக்கும் பாதைகள்',
            'popular.explore': 'அனைத்து பாதைகளையும் காண்க',
            'popular.fromPrefix': 'ரூ.',

            // How It Works Steps
            'steps.eyebrow': 'எளிய பயன்பாடு',
            'steps.title': 'நான்கு எளிய படிகளில் தேடலில் இருந்து இருக்கை வரை.',
            'steps.step1.title': 'தேடுங்கள்',
            'steps.step1.desc': 'எங்கு செல்கிறீர்கள், எப்போது செல்கிறீர்கள் என்று கூறுங்கள்.',
            'steps.step2.title': 'தேர்வுசெய்க',
            'steps.step2.desc': 'பேருந்துகளை ஒப்பிட்டு உங்கள் இருக்கையைத் தேர்வுசெய்யுங்கள்.',
            'steps.step3.title': 'பணம் செலுத்துக',
            'steps.step3.desc': 'இணையம் வழியாக பாதுகாப்பாக கட்டணம் செலுத்துங்கள்.',
            'steps.step4.title': 'பயணிக்கவும்',
            'steps.step4.desc': 'மின்-டிக்கெட்டைக் காட்டி மகிழ்வுடன் பயணியுங்கள்.',

            // Contact & 24/7 Support
            'contact.eyebrow': '24/7 நாடளாவிய உதவி',
            'contact.title': 'உங்கள் பயணம் இனிதாக உதவ நாங்கள் காத்திருக்கிறோம்',
            'contact.desc': 'பேருந்து அட்டவணை, இருக்கை முன்பதிவு, ரத்துசெய்தல் அல்லது இழக்கப்பட்ட உடமைகள் பற்றிய கேள்விகள் உள்ளதா? எங்களை எந்த நேரத்திலும் தொடர்பு கொள்ளலாம்.',
            'contact.phone.title': 'தொலைபேசி & வாட்ஸ்அப் உதவி எண்',
            'contact.phone.text': 'பயண உதவி, முன்பதிவு மற்றும் ரத்துசெய்தல் விவகாரங்களுக்கு 24 மணி நேரமும் செயல்படுகிறது.',
            'contact.phone.badge': 'சராசரி பதில் நேரம் < 2 நிமிடங்கள்',
            'contact.terminal.title': 'பஸ் நிலைய உதவி மையங்கள்',
            'contact.terminal.text': 'முக்கிய நகர பஸ் நிலையங்களில் நேரடி உதவி முகப்புகள்:',
            'contact.term.colombo': 'பாஸ்டியன் மாவத்தை நிலையம் (கவுண்டர் 4)',
            'contact.term.kandy': 'குட்ஸ் ஷெட் இண்டர்சிட்டி நிலையம் (பே 2)',
            'contact.term.galle': 'காலி மத்திய பஸ் நிலையம் (கவுண்டர் 1)',
            'contact.term.jaffna': 'யாழ்ப்பாணம் மத்திய எக்ஸ்பிரஸ் முகப்பு',
            'contact.form.title': 'எங்களுக்கு செய்தி அனுப்புங்கள்',
            'contact.form.subtitle': 'கீழே உள்ள படிவத்தை நிரப்பவும், எங்கள் பிரதிநிதி உடனே பதிலளிப்பார்.',
            'contact.form.name': 'முழுப் பெயர் *',
            'contact.form.namePlaceholder': 'உ.ம். தினேஷ் பெரேரா',
            'contact.form.email': 'மின்னஞ்சல் முகவரி *',
            'contact.form.emailPlaceholder': 'you@example.com',
            'contact.form.phone': 'தொலைபேசி எண் *',
            'contact.form.phonePlaceholder': '+94 77 123 4567',
            'contact.form.subject': 'நாங்கள் எவ்வாறு உதவலாம்? *',
            'contact.form.selectSubject': 'ஒரு தலைப்பைத் தேர்ந்தெடுக்கவும்',
            'contact.form.subBooking': 'டிக்கெட் முன்பதிவு & இருக்கை நிலவரம்',
            'contact.form.subCancel': 'ரத்துசெய்தல் & பணம் திரும்பப் பெறுதல்',
            'contact.form.subLost': 'இழந்த உடமைகள் விவகாரம்',
            'contact.form.subSched': 'புறப்படும் நேர அட்டவணை & தாமதங்கள்',
            'contact.form.subOperator': 'பேருந்து உரிமையாளர் & பதிவு',
            'contact.form.subOther': 'பொதுவான கருத்துகள் & பிற',
            'contact.form.message': 'உங்கள் செய்தி *',
            'contact.form.msgPlaceholder': 'நாங்கள் உங்களுக்கு எவ்வாறு உதவலாம் என்று குறிப்பிடவும்...',
            'contact.form.submit': 'செய்தியை அனுப்புக',

            // Footer
            'footer.tagline': 'டிக்கெட்டை விட மேலானது.<br>சிறந்த பயண வழி.',
            'footer.links.offers': 'சலுகைகள்',
            'footer.links.how': 'செயல்பாடுகள்',
            'footer.links.help': 'உதவி மையம்',
            'footer.links.contact': 'தொடர்பு கொள்க',
            'footer.copy': '© 2026 கோபஸ் இலங்கை (GoBus Sri Lanka)',
            'footer.secure': 'பாதுகாப்பான பரிவர்த்தனைகள் · அனைத்து பயணிகளுக்கும்',
            'footer.terms': 'தனியுரிமை · விதிமுறைகள்',

            // Seat Modal
            'modal.seat.eyebrow': 'இருக்கையைத் தேர்வுசெய்யவும்',
            'modal.seat.driver': 'ஓட்டுநர்',
            'modal.seat.available': 'கிடைக்கக்கூடியவை',
            'modal.seat.selected': 'தேர்வுசெய்தவை',
            'modal.seat.occupied': 'முன்பதிவு செய்தவை',
            'modal.seat.selectedLabel': 'தேர்ந்தெடுக்கப்பட்ட இருக்கை',
            'modal.seat.choosePrompt': 'ஓர் இருக்கையைத் தேர்வுசெய்க',
            'modal.seat.seatPrefix': 'இருக்கை',
            'modal.seat.continue': 'தொடர்க',

            // Chatbot
            'chatbot.title': 'கோபஸ் பயண உதவியாளர்',
            'chatbot.status': '24/7 AI இயக்கம் • எப்போதும் இணைப்பில்',
            'chatbot.placeholder': 'வழிகள், கட்டணங்கள், நேரங்கள் பற்றி கேளுங்கள்…',
            'chatbot.footer': 'வழங்குவது GoBus AI',

            // Auth
            'auth.welcomeBack': 'மீண்டும் வருக',
            'auth.username': 'பயனர் பெயர் அல்லது மின்னஞ்சல்',
            'auth.usernamePlaceholder': 'பயனர் பெயர் அல்லது மின்னஞ்சலை உள்ளிடவும்',
            'auth.password': 'கடவுச்சொல்',
            'auth.passwordPlaceholder': 'உங்கள் கடவுச்சொல்லை உள்ளிடவும்',
            'auth.remember': 'என்னை நினைவில் கொள்க',
            'auth.forgot': 'கடவுச்சொல் மறந்துவிட்டதா?',
            'auth.signIn': 'உள்நுழைக',
            'auth.noAccount': 'கணக்கு இல்லையா?',
            'auth.signUp': 'பதிவு செய்க'
        },

        si: {
            // Navigation
            'nav.book': 'ප්‍රවේශපත්‍ර වෙන්කරන්න',
            'nav.offers': 'විශේෂ දීමනා',
            'nav.howItWorks': 'ක්‍රියාකාරීත්වය',
            'nav.contact': 'සහාය සහ සම්බන්ධතාව',
            'nav.login': 'ඇතුල් වන්න',
            'nav.dashboard': 'මගේ ගිණුම',
            'nav.logout': 'ඉවත් වන්න',

            // Hero
            'hero.eyebrow': 'ශ්‍රී ලංකාවේ බුද්ධිමත්ම බස් වෙන්කිරීමේ සේවාව',
            'hero.title_part1': 'දුර බැහැර ගමන.',
            'hero.title_part2': 'සුවපහසු අත්දැකීම.',
            'hero.text': 'තත්පර කිහිපයකින් ඔබගේ ගමනාන්තය සොයා ගන්න, ඔබ කැමති ආසනය තෝරාගෙන සුවපහසුවෙන් ගමන් කරන්න.',
            'hero.tab.oneWay': 'එක් පැත්තකට',
            'hero.tab.roundTrip': 'යන එන දෙපැත්තට',
            'hero.from': 'පිටත්වන ස්ථානය',
            'hero.to': 'ගමනාන්තය',
            'hero.date': 'ගමන් දිනය',
            'hero.passengers': 'මගීන් ගණන',
            'hero.pax.1': 'මගී 1 යි',
            'hero.pax.2': 'මගීන් 2 යි',
            'hero.pax.3': 'මගීන් 3 යි',
            'hero.pax.4': 'මගීන් 4 යි',
            'hero.searchBtn': 'බස් රථ සොයන්න',
            'hero.secureNote': 'ආරක්ෂිත වෙන්කිරීම · ක්ෂණික ඊ-ප්‍රවේශපත්‍රය · සජීවී ආසන ලබාගැනීමේ හැකියාව',
            'hero.art.tag': 'ලංකාව පුරා<br>සංචාරය කරන්න',
            'hero.art.route': 'කොළඹ ➔ මහනුවර',

            // Cities
            'city.colombo': 'කොළඹ',
            'city.kandy': 'මහනුවර',
            'city.jaffna': 'යාපනය',
            'city.galle': 'ගාල්ල',
            'city.badulla': 'බදුල්ල',

            // Results Section
            'results.eyebrow': 'ලබාගත හැකි ගමන්වාර',
            'results.title': 'ඔබගේ බස් රථය තෝරන්න',
            'results.edit': 'සෙවීම සංස්කරණය',
            'results.today': 'අද',
            'results.busesFound': 'බස් රථ හමුවිය',
            'results.seatsLeft': 'ආසන ඉතිරිව ඇත',
            'results.fromPrice': 'මිල',
            'results.chooseSeats': 'ආසන තෝරන්න',
            'results.soldOut': 'සියලු ආසන වෙන්වී ඇත',
            'results.noBuses': 'බස් රථ හමුනොවීය',
            'results.noBusesMsg': '{source} සිට {destination} දක්වා මෙම දිනයේ බස් රථ නොමැත.',
            'results.noBusesHint': 'වෙනත් දිනයක් තෝරා බලන්න.',

            // Offers Section
            'offers.eyebrow': 'නව ගමනාන්ත',
            'offers.title': 'ඔබගේ ඊළඟ ගමනට වට්ටම්',
            'offers.viewAll': 'සියලු දීමනා බලන්න',
            'offers.tag.limited': 'සීමිත කාලයක් සඳහා',
            'offers.card1.title': 'ඇල්ලට සතිඅන්ත<br>චාරිකාවක්',
            'offers.card1.text': 'තෝරාගත් මාර්ග සඳහා 20% දක්වා වට්ටම්',
            'offers.tag.goFurther': 'දුර ගමන්',
            'offers.card2.title': 'කොළඹ සිට<br>යාපනයට',
            'offers.card2.text': 'උතුරුකරයට සුඛෝපභෝගී ගමනක්',
            'offers.tag.new': 'අලුත්ම සේවාවන්',
            'offers.card3.title': 'රාත්‍රී සීඝ්‍රගාමී<br>සේවාවන්',
            'offers.card3.text': 'අඩු මුදලට පහසු ගමනක්',

            // Why section
            'why.eyebrow': 'නවමු සංචාරක අත්දැකීමක්',
            'why.title': 'පහසු සුන්දර ගමනකට අවශ්‍ය සියල්ල එකම තැනකින්.',
            'why.desc': 'සෙවීමේ සිට ගමනාන්තය දක්වා ඔබගේ සංචාරය ඉතාමත් පහසු සහ විශ්වාසදායක කරමු.',
            'why.btn': 'වැඩිදුර විස්තර',
            'why.benefit1.title': 'ඔබේ කැමැත්ත පරිදි ආසනය',
            'why.benefit1.desc': 'සජීවී ආසන සටහන බලා ඔබ කැමතිම ආසනය වෙන්කරගන්න.',
            'why.benefit2.title': 'ඩිජිටල් ප්‍රවේශපත්‍රය',
            'why.benefit2.desc': 'ක්ෂණිකව ඔබගේ දුරකථනයට සහ ඊමේල් ලිපිනයට ඊ-ටිකට්පත ලැබේ.',
            'why.benefit3.title': 'නිවැරදි වේලාවට',
            'why.benefit3.desc': 'නියමිත වේලාවන් සහ ගමන් විස්තර කල්තියා දැනගන්න.',

            // Popular Routes
            'popular.eyebrow': 'ජනප්‍රිය ගමන් මාර්ග',
            'popular.title': 'වැඩිපුරම ගමන් ගන්නා මාර්ග',
            'popular.explore': 'සියලු මාර්ග බලන්න',
            'popular.fromPrefix': 'රු.',

            // How It Works Steps
            'steps.eyebrow': 'පහසු ක්‍රියාපිළිවෙල',
            'steps.title': 'පියවර හතරකින් ඔබගේ ගමන සැලසුම් කරන්න.',
            'steps.step1.title': 'සොයන්න',
            'steps.step1.desc': 'ඔබ යන්නේ කොහේද කවදාද යන්න තෝරන්න.',
            'steps.step2.title': 'තෝරන්න',
            'steps.step2.desc': 'බස් රථ සංසන්දනය කර ආසන තෝරාගන්න.',
            'steps.step3.title': 'ගෙවන්න',
            'steps.step3.desc': 'මාර්ගගත ක්‍රමයට ආරක්ෂිතව මුදල් ගෙවන්න.',
            'steps.step4.title': 'ගමන අරඹන්න',
            'steps.step4.desc': 'ඊ-ටිකට්පත පෙන්වා සතුටින් ගමන් කරන්න.',

            // Contact & 24/7 Support
            'contact.eyebrow': '24/7 දිවයින පුරා සේවාව',
            'contact.title': 'ඔබගේ ගමන පහසු කිරීමට අප නිරතුරුව සූදානම්',
            'contact.desc': 'බස් කාලසටහන්, ආසන වෙන්කිරීම් හෝ අවලංගු කිරීම් පිළිබඳව ඕනෑම වේලාවක අප අමතන්න.',
            'contact.phone.title': 'දුරකථන සහ වට්ස්ඇප් සේවාව',
            'contact.phone.text': 'පැය 24 පුරා ක්‍රියාත්මක කඩිනම් පාරිභෝගික සහාය සේවාව.',
            'contact.phone.badge': 'සාමාන්‍ය ප්‍රතිචාර කාලය < මිනිත්තු 2',
            'contact.terminal.title': 'ප්‍රධාන බස් නැවතුම්පොළ කාර්යාල',
            'contact.terminal.text': 'දිවයිනේ ප්‍රධාන අන්තර්නගර බස් නැවතුම්පොළවල ක්ෂණික සහාය කවුළු:',
            'contact.term.colombo': 'බැස්ටියන් මාවත පර්යන්තය (කවුළු අංක 4)',
            'contact.term.kandy': 'ගුඩ්ස් ෂෙඩ් අන්තර්නගර පර්යන්තය (බේ 2)',
            'contact.term.galle': 'ගාල්ල මධ්‍යම බස් නැවතුම්පොළ (කවුළු අංක 1)',
            'contact.term.jaffna': 'යාපනය මධ්‍යම එක්ස්ප්‍රස් කවුළුව',
            'contact.form.title': 'අපට පණිවිඩයක් එවන්න',
            'contact.form.subtitle': 'පහත පෝරමය පුරවා එවන්න, අපගේ නියෝජිතයෙකු වහාම සම්බන්ධ වනු ඇත.',
            'contact.form.name': 'සම්පූර්ණ නම *',
            'contact.form.namePlaceholder': 'උදා: දිනේෂ් පෙරේරා',
            'contact.form.email': 'ඊමේල් ලිපිනය *',
            'contact.form.emailPlaceholder': 'you@example.com',
            'contact.form.phone': 'දුරකථන අංකය *',
            'contact.form.phonePlaceholder': '+94 77 123 4567',
            'contact.form.subject': 'අපට ඔබට උදව් කළ හැක්කේ කෙසේද? *',
            'contact.form.selectSubject': 'මාතෘකාවක් තෝරන්න',
            'contact.form.subBooking': 'ප්‍රවේශපත්‍ර වෙන්කිරීම සහ ආසන ලබාගැනීම',
            'contact.form.subCancel': 'අවලංගු කිරීම් සහ මුදල් ආපසු ලබාගැනීම',
            'contact.form.subLost': 'නැතිවූ බඩුබාහිරාදිය පිළිබඳව',
            'contact.form.subSched': 'බස් රථ ධාවන කාලසටහන්',
            'contact.form.subOperator': 'බස් හිමිකරුවන්ගේ ලියාපදිංචිය',
            'contact.form.subOther': 'වෙනත් කරුණු',
            'contact.form.message': 'ඔබගේ පණිවිඩය *',
            'contact.form.msgPlaceholder': 'ඔබට අවශ්‍ය සහාය මෙහි ලියන්න...',
            'contact.form.submit': 'පණිවිඩය යවන්න',

            // Footer
            'footer.tagline': 'ප්‍රවේශපත්‍රයකට වඩා එහා ගිය.<br>සුවපහසු ගමනක්.',
            'footer.links.offers': 'දීමනා',
            'footer.links.how': 'ක්‍රියාකාරීත්වය',
            'footer.links.help': 'උදවු මධ්‍යස්ථානය',
            'footer.links.contact': 'සම්බන්ධ වන්න',
            'footer.copy': '© 2026 GoBus ශ්‍රී ලංකා',
            'footer.secure': 'ආරක්ෂිත ගෙවීම් · සෑම ගමනක් සඳහාම',
            'footer.terms': 'පෞද්ගලිකත්වය · කොන්දේසි',

            // Seat Modal
            'modal.seat.eyebrow': 'ආසනය තෝරන්න',
            'modal.seat.driver': 'රියදුරු',
            'modal.seat.available': 'හිස් ආසන',
            'modal.seat.selected': 'තෝරාගත්',
            'modal.seat.occupied': 'වෙන්වූ ආසන',
            'modal.seat.selectedLabel': 'තෝරාගත් ආසනය',
            'modal.seat.choosePrompt': 'ආසනයක් තෝරන්න',
            'modal.seat.seatPrefix': 'ආසන අංක',
            'modal.seat.continue': 'ඉදිරියට යන්න',

            // Chatbot
            'chatbot.title': 'GoBus සහායක',
            'chatbot.status': '24/7 AI සේවාව • සක්‍රීයයි',
            'chatbot.placeholder': 'මාර්ග, කාලසටහන්, ගාස්තු ගැන අසන්න…',
            'chatbot.footer': 'GoBus AI මගින් බලගැන්වේ',

            // Auth
            'auth.welcomeBack': 'නැවත සාදරයෙන් පිළිගනිමු',
            'auth.username': 'පරිශීලක නාමය හෝ ඊමේල්',
            'auth.usernamePlaceholder': 'පරිශීලක නාමය හෝ ඊමේල් ඇතුල් කරන්න',
            'auth.password': 'මුරපදය',
            'auth.passwordPlaceholder': 'ඔබගේ මුරපදය ඇතුල් කරන්න',
            'auth.remember': 'මතක තබාගන්න',
            'auth.forgot': 'මුරපදය අමතකද?',
            'auth.signIn': 'ඇතුල් වන්න',
            'auth.noAccount': 'ගිණුමක් නොමැතිද?',
            'auth.signUp': 'ලියාපදිංචි වන්න'
        }
    };

    const LANG_LABELS = {
        en: { name: 'English', badge: 'EN', flag: '🇬🇧' },
        ta: { name: 'தமிழ் (Tamil)', badge: 'TA', flag: '🇱🇰' },
        si: { name: 'සිංහල (Sinhala)', badge: 'SI', flag: '🇱🇰' }
    };

    let currentLang = DEFAULT_LANG;

    function getSavedLanguage() {
        try {
            return localStorage.getItem(STORAGE_KEY) || DEFAULT_LANG;
        } catch (e) {
            return DEFAULT_LANG;
        }
    }

    function translateText(key, fallback = '') {
        const langDict = translations[currentLang] || translations[DEFAULT_LANG];
        return langDict[key] || translations[DEFAULT_LANG][key] || fallback || key;
    }

    function applyTranslations() {
        document.documentElement.lang = currentLang;

        // Translate textContent and innerHTML elements
        document.querySelectorAll('[data-i18n]').forEach(el => {
            const key = el.getAttribute('data-i18n');
            const val = translateText(key);
            if (val) {
                if (val.includes('<') && val.includes('>')) {
                    el.innerHTML = val;
                } else {
                    el.textContent = val;
                }
            }
        });

        // Translate placeholders
        document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
            const key = el.getAttribute('data-i18n-placeholder');
            const val = translateText(key);
            if (val) el.placeholder = val;
        });

        // Translate titles
        document.querySelectorAll('[data-i18n-title]').forEach(el => {
            const key = el.getAttribute('data-i18n-title');
            const val = translateText(key);
            if (val) el.title = val;
        });

        // Update city select options in search form if present
        ['from-city', 'to-city'].forEach(selectId => {
            const sel = document.getElementById(selectId);
            if (sel) {
                Array.from(sel.options).forEach(opt => {
                    const originalCity = opt.getAttribute('data-city-key') || opt.value.toLowerCase();
                    if (!opt.getAttribute('data-city-key')) {
                        opt.setAttribute('data-city-key', originalCity);
                    }
                    const cityTranslation = translateText(`city.${originalCity}`);
                    if (cityTranslation) {
                        opt.textContent = cityTranslation;
                    }
                });
            }
        });

        // Update language switcher UI
        updateLanguageDropdownUI();

        // Dispatch custom event for dynamic components
        window.dispatchEvent(new CustomEvent('gobus:languageChanged', {
            detail: { language: currentLang, translations: translations[currentLang] }
        }));
    }

    function updateLanguageDropdownUI() {
        const currentLabel = document.getElementById('current-lang-label');
        const currentBadge = document.getElementById('current-lang-badge');
        const info = LANG_LABELS[currentLang] || LANG_LABELS.en;

        if (currentLabel) {
            currentLabel.textContent = info.name;
        }
        if (currentBadge) {
            currentBadge.textContent = info.badge;
        }

        // Update active class on options
        document.querySelectorAll('.lang-option').forEach(btn => {
            const btnLang = btn.getAttribute('data-lang');
            if (btnLang === currentLang) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });
    }

    function setLanguage(lang) {
        if (!translations[lang]) lang = DEFAULT_LANG;
        currentLang = lang;
        try {
            localStorage.setItem(STORAGE_KEY, lang);
        } catch (e) {
            console.warn('Could not save language to localStorage', e);
        }
        applyTranslations();
    }

    function initLanguageDropdown() {
        const toggleBtn = document.getElementById('language-toggle');
        const menu = document.getElementById('language-menu');

        if (!toggleBtn || !menu) return;

        // Toggle dropdown open/closed
        toggleBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            const isOpen = menu.classList.contains('show');
            if (isOpen) {
                menu.classList.remove('show');
                toggleBtn.setAttribute('aria-expanded', 'false');
            } else {
                menu.classList.add('show');
                toggleBtn.setAttribute('aria-expanded', 'true');
            }
        });

        // Language option selection
        document.querySelectorAll('.lang-option').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.stopPropagation();
                const lang = btn.getAttribute('data-lang');
                if (lang) {
                    setLanguage(lang);
                    menu.classList.remove('show');
                    toggleBtn.setAttribute('aria-expanded', 'false');
                }
            });
        });

        // Close on outside click
        document.addEventListener('click', (e) => {
            if (!menu.contains(e.target) && !toggleBtn.contains(e.target)) {
                menu.classList.remove('show');
                toggleBtn.setAttribute('aria-expanded', 'false');
            }
        });

        // Close on Escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && menu.classList.contains('show')) {
                menu.classList.remove('show');
                toggleBtn.setAttribute('aria-expanded', 'false');
            }
        });
    }

    // Expose Global API
    window.GoBusI18n = {
        setLanguage: setLanguage,
        getLanguage: () => currentLang,
        t: translateText,
        availableLanguages: Object.keys(translations),
        langLabels: LANG_LABELS
    };

    // Auto-initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            currentLang = getSavedLanguage();
            initLanguageDropdown();
            applyTranslations();
        });
    } else {
        currentLang = getSavedLanguage();
        initLanguageDropdown();
        applyTranslations();
    }
})();
