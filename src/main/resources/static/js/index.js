'use strict';


$().ready(function() {
  const download_link = $("#download_link");
  const download_basic_url = download_link.attr('href') + '?';
  let download_params = {
    lang: $('#resume_lang').text(),
    format: $('#resume_format').text()
  };

  function updateDownloadResumeUrl() {
    download_link.attr('href', download_basic_url + $.param(download_params));
  }

  updateDownloadResumeUrl();
  $("#resume_lang_container *").click(function() {
    let lang = $(this).text();
    $("#resume_lang").text(lang);
    download_params.lang = lang;
    updateDownloadResumeUrl();
  });
  $("#resume_format_container *").click(function() {
    let format = $(this).text();
    $("#resume_format").text(format);
    download_params.format = format;
    updateDownloadResumeUrl();
  });
});
