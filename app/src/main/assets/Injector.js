/** home_feed **/
const article_element = document.querySelector('article[role="presentation"]');
if (article_element){
    article_element.parentElement.remove();
}
/** END **/

/** reel_feed **/
const reel_icon_element = document.querySelector('a[href="/reels/"]');
if (reel_icon_element) {
    reel_icon_element.remove();
}
/** END **/

/** story_strip **/
const story_strip = document.querySelector('div[role="menu"]');
if (story_strip) {
    story_strip.remove();
}
/** END **/

/** search_feed **/
const explore_icon = document.querySelector('a[href="/explore/"]');
if (explore_icon){
    explore_icon.href = "https://www.instagram.com/explore/search/";

    var event_listner = function(event) {
        event.stopImmediatePropagation();
    };

    explore_icon.removeEventListener("click", event_listner);

    explore_icon.addEventListener("click", event_listner, true);

    const cancel_button = document.querySelector('div[class="x9f619 xjbqb8w x78zum5 x168nmei x13lgxp2 x5pf9jr xo71vjh x16n37ib x1n2onr6 x1plvlek xryxfnj x1c4vz4f x2lah0s xdt5ytf xqjyukv x1qjc9v5 x1oa3qoh xl56j7k"]');
    if (cancel_button){
        cancel_button.remove();
    }
} 
/** END **/

/** use_followed_feed **/
const home_icon = document.querySelector('a[href="/"]');
if (home_icon){
    home_icon.href = "/?variant=following";

    var event_listner = function(event) {
        event.stopImmediatePropagation();
    };

    home_icon.removeEventListener("click", event_listner);

    home_icon.addEventListener("click", event_listner, true);
}
const back_to_home = document.querySelector('div[class="x78zum5"]');
if (back_to_home) {
    var event_listner = function(event) {
        window.location.href = "/?variant=following";
        event.stopImmediatePropagation();
    };

    back_to_home.removeEventListener("click", event_listner);

    back_to_home.addEventListener("click", event_listner, true);
}
/** END **/

/** ALWAYS_EXECUTE **/
const use_app_banner = document.querySelector('div[class="_acc8 _abpk"]');
if (use_app_banner) {
    use_app_banner.remove();
}

const settings_icon = document.querySelector('a[href="/accounts/settings/"]');
if (settings_icon){
    Android.setSettingsMenuButton();
} else {
    Android.deleteSettingsMenuButton();
}
/** END **/

