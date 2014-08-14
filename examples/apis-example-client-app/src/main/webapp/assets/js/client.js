$(function() {
	// show the correct step
	$('#' + $('input#step').val()).collapse('show');

	// we are in step 3 of implicit grant
//	if ($('#parseAnchorForAccesstoken').val() == 'true') {
//		value = window.location.hash.replace("#", "");
//		$('#parseAnchorForAccesstoken').val('');
//		$.get('/v1/test/parseAnchor.shtml?' + value, function(data) {
//			$('#responseInfo').html(data);
//			$.each(data.split("&"), function(i, value) {
//				param = value.split("=");
//				if (param[0] == 'access_token') {
//					$('#accessTokenValue').html(param[1]);
//				}
//			});
//		});
//	}
});
