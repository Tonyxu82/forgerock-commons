/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */

//<![CDATA[

ZeroClipboard.config({
        /* swfPath:  "includes/swf/ZeroClipboard.swf", */
        swfPath:  "http://cdnjs.cloudflare.com/ajax/libs/zeroclipboard/2.2.0/ZeroClipboard.swf",
        trustedDomains: ["*"],
        forceEnhancedClipboard: true,
        forceHandCursor: true,
        debug: true}
);



var wrapConfigurablesFn = function () {
    $('*:contains("https://openam.example.com:8443")').each(function(){
        if($(this).children().length < 1)
            $(this).html(
                $(this).text().replace(new RegExp("https://openam.example.com:8443", 'g'),'<span class="exampleUrl">https://openam.example.com:8443</span>')
            )
    });
    $('*:contains("amadmin")').each(function(){
        if($(this).children().length < 1)
            $(this).html(
                $(this).text().replace(new RegExp("amadmin", 'g'),'<span class="exampleAdmin">amadmin</span>')
            )
    });
    $('*:contains("iPlanetDirectoryPro")').each(function(){
        if($(this).children().length < 1)
            $(this).html(
                $(this).text().replace(new RegExp("iPlanetDirectoryPro", 'g'),'<span class="exampleSsoCookieName">iPlanetDirectoryPro</span>')
            )
    });
};

var btnClickHandler = function () {
    $("button#applyDocUpdate").click(function(){

        $('span.exampleUrl').fadeOut("slow", function(){
            $('span.exampleUrl').text($("input#exampleUrl").val());
            $('span.exampleUrl').fadeIn("slow");
        });

        $('span.exampleAdmin').fadeOut("slow", function(){
            $('span.exampleAdmin').text($("input#exampleAdmin").val());
            $('span.exampleAdmin').fadeIn("slow");
        });

        $('span.exampleSsoCookieName').fadeOut("slow", function(){
            $('span.exampleSsoCookieName').text($("input#exampleSsoCookieName").val());
            $('span.exampleSsoCookieName').fadeIn("slow");
        });

        $('#docConfig').modal('hide');
    });
};


var addCopyButtonFN = function () {
    $( ".cmdline" ).each(function() {
        $(this).before('<div class="zero-clipboard hidden-xs"><span class="btn-copy-cmdline"><span class="glyphicon glyphicon-pencil"></span> Copy<span class="hidden-sm"> command to clipboard</span></span></div>');
    });

    $( ".codelisting" ).each(function() {
        $(this).before('<div class="zero-clipboard hidden-xs"><span class="btn-copy-codelisting"><span class="glyphicon glyphicon-pencil"></span> Copy<span class="hidden-sm"> code to clipboard</span></span></div>');
    });
};
var enableScrollSpyFN = function () {
    $('body').scrollspy({
        target: '#sidebar',
        offset: 16
    });
};

var enableToolTipFN = function () {
    $(function () {
        $("[data-toggle='tooltip']").tooltip({placement: 'bottom'});
    });
};

var enableBackToTopFadeInFN = function () {
    var offset = 220;
    var duration = 500;
    $(window).scroll(function() {
        if ($(this).scrollTop() > offset) {
            $('.back-to-top').fadeIn(duration);
        } else {
            $('.back-to-top').fadeOut(duration);
        }
    });

    $('.back-to-top').click(function(event) {
        event.preventDefault();
        $('html, body').animate({scrollTop: 0}, duration);
        return false;
    })
};

var enableClampedWidthsFN = function () {

    /*
     * Clamped-width.
     * Usage:
     *  <div data-clampedwidth=".myParent">This long content will force clamped width</div>
     *
     * Author: LV
     */
    $('[data-clampedwidth]').each(function () {
        var elem = $(this);
        var parentPanel = elem.data('clampedwidth');
        var resizeFn = function () {
            var sideBarNavWidth = $(parentPanel).width() - parseInt(elem.css('paddingLeft')) - parseInt(elem.css('paddingRight')) - parseInt(elem.css('marginLeft')) - parseInt(elem.css('marginRight')) - parseInt(elem.css('borderLeftWidth')) - parseInt(elem.css('borderRightWidth'));
            elem.css('width', sideBarNavWidth);
        };

        resizeFn();
        $(window).resize(resizeFn);
    });
};



var affixToCFN = function() {
    $('#sidebar').affix({
        offset: {
            top: function () {
                return (this.top = $('.jumbotron').outerHeight(true)-66)
            }
        }
    });
};

var attachAnchorsToHeadings = function() {
    addAnchors('h1.title, h2.title, h3.title, h4.title, h5.title');
}

var addZeroClipboardToCmdlineButtonsFN = function () {
    var copycmdline = new ZeroClipboard( $('.btn-copy-cmdline') );

    copycmdline.on( 'ready', function(event) {
        // console.log( 'movie is loaded' );

        copycmdline.on( 'copy', function(event) {
            var wrappedText = "";
            $(event.target).parent().next().find("strong").each(function(index) {
                wrappedText += $(this).text() + "\n";
            });
            event.clipboardData.setData('text/plain', wrappedText);
        });

        copycmdline.on( 'aftercopy', function(event) {
            // console.log('Copied to clipboard: ' + event.data['text/plain']);
            $(event.target).parent().next().find("strong").each(function(index) {
                $(this).effect("transfer", { to: $(event.target)}, 750);
            });
        });
    });

    copycmdline.on( 'error', function(event) {
        console.log( 'ZeroClipboard error of type "' + event.name + '": ' + event.message );
        ZeroClipboard.destroy();
    });
};

var addZeroClipboardToCodeButtonsFN = function () {
    var copycodelisting = new ZeroClipboard( $('.btn-copy-codelisting') );

    copycodelisting.on( 'ready', function(event) {
        //console.log( 'movie is loaded' );

        copycodelisting.on( 'copy', function(event) {
            var wrappedText = "";
            $(event.target).parent().next(".codelisting.linenums").children().contents().each(function(index) {
                wrappedText += $(this).text() + "\n";
            });
            if(wrappedText == "") {
                wrappedText = $(event.target).parent().next(".codelisting").contents().text();
            };
            event.clipboardData.setData('text/plain', wrappedText);
        });

        copycodelisting.on( 'aftercopy', function(event) {
            // console.log('Copied code to clipboard: ' + event.data['text/plain']);
            $(event.target).parent().next(".codelisting").first().effect("transfer", { to: $(event.target)}, 750);
        });
    });

    copycodelisting.on( 'error', function(event) {
        console.log( 'ZeroClipboard error of type "' + event.name + '": ' + event.message );
        ZeroClipboard.destroy();
    });
};



$(document).ready(function() {
    //wrapConfigurablesFn();
    btnClickHandler();
    addCopyButtonFN();
    addZeroClipboardToCmdlineButtonsFN();
    addZeroClipboardToCodeButtonsFN();
    enableToolTipFN();
    enableBackToTopFadeInFN();
    enableClampedWidthsFN();
    enableScrollSpyFN();
    affixToCFN();
    prettyPrint();
    attachAnchorsToHeadings();
    $(".fancybox").fancybox({
        padding : 0,
        helpers : {
            overlay : {
                css : {
                    'background' : 'rgba(66, 130, 116, 0.6)'
                }
            }
        },
        beforeShow : function() {
            var alt = this.element.find('img').attr('alt');

            this.inner.find('img').attr('alt', alt);

            this.title = alt;
        }
    });
});

//]]>
