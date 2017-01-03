ACC.worldpayQuote = {
	DATA_SUBMIT_BUTTON: 'submitButton',
	DATA_MODAL_TITLE_LABEL: 'modalTitleLabel',
	DATA_MODAL_INPUT_LABEL: 'modalInputLabel',
	quoteDecisionSubmissionEnabled: false,

	_autoload: [
		"bindQuoteAcceptModal"
	],

	bindQuoteAcceptModal: function()
	{
		$(document).on('click','.acceptQuoteOrderDecisionForm .payByCard' ,function(e) {
			e.preventDefault();

			var form = $(this).closest('form');
			var title = $(this).data(ACC.worldpayQuote.DATA_MODAL_TITLE_LABEL);
			var inputLabel = $(this).data(ACC.worldpayQuote.DATA_MODAL_INPUT_LABEL);
			var quoteAcceptModal = form.find('.quoteAcceptModal');

			// Cannot be initialized in onOpen
			ACC.worldpayQuote.initQuoteAcceptModal(quoteAcceptModal, inputLabel);

			ACC.colorbox.open(title, {
				href: quoteAcceptModal,
				inline: true,
				width: '400px',
				onOpen: function () {
				},
				onComplete: function () {
				},
				onClosed: function () {
					if (ACC.worldpayQuote.quoteDecisionSubmissionEnabled) {
						form.submit();
					}
				}
			});
		});

		$(document).on('click','.quoteAcceptModal .submitQuoteAcceptButton',function(e) {
			e.preventDefault();

			ACC.worldpayQuote.quoteDecisionSubmissionEnabled = true;
			ACC.colorbox.close();
		});

		$(document).on('click','.quoteAcceptModal .cancelQuoteAcceptButton',function(e) {
			e.preventDefault();

			ACC.colorbox.close();
		});

		$('.quoteAcceptModal input[name=securityCode]').keyup(function() {
			var submitButton = $(this).data(ACC.worldpayQuote.DATA_SUBMIT_BUTTON);
			submitButton.prop('disabled', this.value == "" ? true : false);
		});
	},

	initQuoteAcceptModal: function(quoteAcceptModal, inputLabel)
	{
		var securityCode = quoteAcceptModal.find('input[name=securityCode]');
		var securityCodeLabel = quoteAcceptModal.find('.headline');
		var submitButton = quoteAcceptModal.find('.submitQuoteAcceptButton');

		securityCode.val('');
		securityCode.data(ACC.worldpayQuote.DATA_SUBMIT_BUTTON, submitButton);

		securityCodeLabel.text(inputLabel);

		submitButton.prop('disabled', true);

		ACC.worldpayQuote.quoteDecisionSubmissionEnabled = false;
	}
};
