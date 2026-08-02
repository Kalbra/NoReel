/** ALWAYS_EXECUTE **/
const use_app_banner = document.querySelector('div[class="_acc8 _abpk"]');
if (use_app_banner) {
    use_app_banner.style.display = "none";
}

const settings_icon = document.querySelector('a[href="/accounts/settings/?entrypoint=profile"]');
if (settings_icon){
    Android.setSettingsMenuButton();
} else {
    Android.deleteSettingsMenuButton();
}

var parser = document.createElement('a');
parser.href = document.location.href;
if(parser.hostname != "www.instagram.com" && parser.hostname != ""){
    Android.openInStdBrowser(parser.href);
    document.location.href = "https://instagram.com";
}

document.body.style.setProperty('-webkit-tap-highlight-color', "transparent");
/** END **/

/** home_feed **/
const article_element = document.querySelector('div[class="html-div xdj266r x14z9mp xat24cr x1lziwak xexx8yu xyri2b x18d9i69 x1c1uobl x9f619 xf68679 x5lhr3w xjbqb8w x78zum5 x15mokao x1ga7v0g x16uus16 xbiv7yw x1uhb9sk x1plvlek xryxfnj x1c4vz4f x2lah0s xdt5ytf xqjyukv x1qjc9v5 x1oa3qoh x1nhvcw1"]');
if (article_element){
    article_element.parentElement.style.display = "none";
}
/** END **/

/** home_feed_reels **/
if (document.location.pathname == "/") {
    const home_reel_links = document.querySelectorAll('a[href*="/reel/"], a[href*="/reels/audio/"]');
    for (var reel_link_index = 0; reel_link_index < home_reel_links.length; reel_link_index++) {
        const home_reel_post = home_reel_links[reel_link_index].closest('article');
        if (home_reel_post) {
            const home_reel_videos = home_reel_post.querySelectorAll('video');
            for (var reel_video_index = 0; reel_video_index < home_reel_videos.length; reel_video_index++) {
                home_reel_videos[reel_video_index].pause();
                home_reel_videos[reel_video_index].muted = true;
            }
            /* The home feed is a virtualized list. "display: none" removes the layout box, which corrupts Instagram's item bookkeeping and strands the user in blank reserved space while scrolling. Collapsing to a measurable 0px box keeps the feed working. */
            const home_reel_style = home_reel_post.style;
            home_reel_style.height = "0px";
            home_reel_style.minHeight = "0px";
            home_reel_style.maxHeight = "0px";
            home_reel_style.margin = "0px";
            home_reel_style.padding = "0px";
            home_reel_style.overflow = "hidden";
        }
    }
}
/** END **/

/** reel_feed **/
const reel_icon_element = document.querySelector('a[href="/reels/"]');
if (reel_icon_element) {
    reel_icon_element.style.display = "none";
}
/** END **/

/** reel_scrolling **/
const reel_scroll_videos = document.querySelectorAll('video');
for (var reel_scroll_index = 0; reel_scroll_index < reel_scroll_videos.length; reel_scroll_index++) {
    var reel_scroll_node = reel_scroll_videos[reel_scroll_index].parentElement;
    while (reel_scroll_node && reel_scroll_node !== document.body) {
        const reel_scroll_height = reel_scroll_node.clientHeight;
        const reel_scroll_overflow = window.getComputedStyle(reel_scroll_node).overflowY;
        if (reel_scroll_height > 200 && reel_scroll_node.scrollHeight > reel_scroll_height * 2 && (reel_scroll_overflow == "auto" || reel_scroll_overflow == "scroll") && reel_scroll_node.querySelectorAll('video').length > 1) {
            /* A reel viewer pages one whole screen at a time, so its direct children are each about as tall as the viewport. A chat message list is not built that way, which keeps normal DM scrolling working. */
            var reel_scroll_paged = 0;
            for (var reel_child_index = 0; reel_child_index < reel_scroll_node.children.length; reel_child_index++) {
                const reel_child_height = reel_scroll_node.children[reel_child_index].getBoundingClientRect().height;
                if (reel_child_height > reel_scroll_height * 0.85 && reel_child_height < reel_scroll_height * 1.15) {
                    reel_scroll_paged++;
                }
            }
            if (reel_scroll_paged > 0) {
                reel_scroll_node.style.overflow = "hidden";
                break;
            }
        }
        reel_scroll_node = reel_scroll_node.parentElement;
    }
}
/** END **/

/** story_strip **/
const story_strip = document.querySelector('div[data-pagelet="story_tray"]');
if (story_strip && !settings_icon) {
    story_strip.style.display = "none";
}
/** END **/

/** audio_on **/
try{
if (!window.noreel_audio_enabled) {
    const videos = document.querySelectorAll('video');
    var audio_is_playing = false;
    for (var video_index = 0; video_index < videos.length; video_index++) {
        if (!videos[video_index].muted && videos[video_index].volume > 0) {
            audio_is_playing = true;
        }
    }
    if (audio_is_playing) {
        window.noreel_audio_enabled = true;
    } else {
        document.querySelector('path[d="M1.5 13.3c-.8 0-1.5.7-1.5 1.5v18.4c0 .8.7 1.5 1.5 1.5h8.7l12.9 12.9c.9.9 2.5.3 2.5-1v-9.8c0-.4-.2-.8-.4-1.1l-22-22c-.3-.3-.7-.4-1.1-.4h-.6zm46.8 31.4-5.5-5.5C44.9 36.6 48 31.4 48 24c0-11.4-7.2-17.4-7.2-17.4-.6-.6-1.6-.6-2.2 0L37.2 8c-.6.6-.6 1.6 0 2.2 0 0 5.7 5 5.7 13.8 0 5.4-2.1 9.3-3.8 11.6L35.5 32c1.1-1.7 2.3-4.4 2.3-8 0-6.8-4.1-10.3-4.1-10.3-.6-.6-1.6-.6-2.2 0l-1.4 1.4c-.6.6-.6 1.6 0 2.2 0 0 2.6 2 2.6 6.7 0 1.8-.4 3.2-.9 4.3L25.5 22V1.4c0-1.3-1.6-1.9-2.5-1L13.5 10 3.3-.3c-.6-.6-1.5-.6-2.1 0L-.2 1.1c-.6.6-.6 1.5 0 2.1L4 7.6l26.8 26.8 13.9 13.9c.6.6 1.5.6 2.1 0l1.4-1.4c.7-.6.7-1.6.1-2.2z"]').parentElement.parentElement.click();
    }
}
} catch(e){}
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
}
const cancel_button = document.querySelector('div[class="html-div xdj266r x14z9mp xat24cr xexx8yu xyri2b x18d9i69 x1c1uobl x9f619 xjbqb8w x78zum5 x15mokao x1ga7v0g x16uus16 xbiv7yw x1diwwjn x1n2onr6 x1plvlek xryxfnj x1c4vz4f x2lah0s xdt5ytf xqjyukv x1qjc9v5 x1oa3qoh xl56j7k"]');
if (cancel_button){
    cancel_button.style.display = "none";
}
/** END **/

/** use_followed_feed **/
if(window.location == "https://www.instagram.com/"){
    window.location = "/?variant=following";
}
/** END **/

/** show_unread_message_count **/
const unread_message_count = document.querySelector('div[class="x4fivb0 xmn1u35 x10l6tqk xn0lweg x1vjfegm"]');
if(unread_message_count){
    unread_message_count.style.display = "none";
}
/** END **/