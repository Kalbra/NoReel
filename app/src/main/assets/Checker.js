/** reel_feed **/
const reel_icon_element = document.querySelector('a[href="/reels/"]');
if (reel_icon_element) {
    reel_icon_element.remove();
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

