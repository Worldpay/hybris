<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:url value="/checkout/worldpay/summary/placeOrder" var="placeOrderUrl" />
<spring:url value="/checkout/worldpay/summary/placeOrder" var="placeOrderUrl" htmlEscape="false" />
<spring:theme code="responsive.replenishmentScheduleForm.activateDaily" var="Daily" />
<spring:theme code="responsive.replenishmentScheduleForm.activateWeekly" var="Weekly" />
<spring:theme code="responsive.replenishmentScheduleForm.activateMonthly" var="Monthly" />
<spring:theme code="text.store.dateformat.datepicker.selection" text="mm/dd/yy" var="dateForForDatePicker" />

<div style="display:none;">
	<div class="clearfix" id="replenishmentSchedule"
		data-date-For-Date-Picker="${dateForForDatePicker}"
		data-place-Order-Form-Replenishment-Recurrence="${placeOrderForm.replenishmentRecurrence}"
		data-date-For-Date-Picker="${fn:escapeXml(dateForForDatePicker)}"
		data-place-Order-Form-Replenishment-Recurrence="${fn:escapeXml(placeOrderForm.replenishmentRecurrence)}"
		data-place-Order-Form-N-Days="${fn:escapeXml(placeOrderForm.nDays)}"
		data-place-Order-Form-Nth-Day-Of-Month="${fn:escapeXml(placeOrderForm.nthDayOfMonth)}"
		data-place-Order-Form-Negotiate-Quote="${placeOrderForm.negotiateQuote}"
		data-place-Order-Form-Replenishment-Order="${placeOrderForm.replenishmentOrder}">


		<div class="column scheduleform  scheduleform_left">
			<div class="replenishmentFrequency_left">
				<div class="form-element-icon datepicker">
					<formElement:formInputBox idKey="replenishmentStartDate" labelKey="replenishmentScheduleForm.startDate" path="replenishmentStartDate" inputCSS="date js-replenishment-datepicker" mandatory="true" />
					<i class="glyphicon glyphicon-calendar js-open-datepicker"></i>
				</div>
			</div>
		</div>

		<div class="replenishmentFrequency">
			<form:radiobutton path="replenishmentRecurrence" id="replenishmentFrequencyD" label="${Daily}" value="DAILY" class="replenishmentfrequencyD" />
		</div>
		<div class="replenishmentFrequency">
			<form:radiobutton path="replenishmentRecurrence" id="replenishmentFrequencyW" label="${Weekly}" value="WEEKLY" class="replenishmentfrequencyW" />
		</div>
		<div class="replenishmentFrequency">
			<form:radiobutton path="replenishmentRecurrence" id="replenishmentFrequencyM" label="${Monthly}" value="MONTHLY" class="replenishmentfrequencyM" />
		</div>

		<div class="column scheduleform scheduleformD" style="display: none;">
			<div class="form-group">
				<label class="control-label" for="nDays">
					<spring:theme code="responsive.replenishmentScheduleForm.daily.days" />
				</label>
				<div class="controls">
					<form:select id="nDays" path="nDays" style="width: 100px;" class="form-control">
						<form:options items="${nDays}" />
					</form:select>
				</div>
			</div>
		</div>

		<div class="column scheduleform scheduleformW" style="display: none;">
			<div class="div_nWeeks1">
				<div class="form-group">
					<label class="control-label" for="nWeeks">
						<spring:theme code="responsive.replenishmentScheduleForm.weekly.weeks" />
					</label>
					<div class="controls">
						<form:select id="nWeeks" path="nWeeks" style="width: 100px;" class="form-control">
							<form:options items="${nthWeek}" />
						</form:select>
					</div>
				</div>
			</div>

			<div class="div_nWeeks2">
				<div>
					<spring:theme code="responsive.replenishmentScheduleForm.weekly.daysOfWeek" />
				</div>
				<div class="row scheduleform-checkboxes">
					<form:checkboxes id="daysOfWeek" items="${daysOfWeek}" itemLabel="name" itemValue="code" path="nDaysOfWeek" element="div class='scheduleform-checkbox col-md-4 col-xs-6'" />
				</div>
			</div>
		</div>

		<div class="column scheduleform scheduleformM" style="display: none;">
			<div class="form-group">
				<label class="control-label" for="nthDayOfMonth">
					<spring:theme code="responsive.replenishmentScheduleForm.monthly.day" />
				</label>
				<div class="controls">
					<form:select id="nthDayOfMonth" path="nthDayOfMonth" style="width: 100px;" class="form-control">
						<form:options items="${nthDayOfMonth}" />
					</form:select>
				</div>
			</div>
		</div>

		<div class="js-replenishment-actions col-xs-12">
            <div class="modal-actions">
                <form:input type="hidden" id="replenishmentOrder" class="replenishmentOrderClass" path="replenishmentOrder" />

                <button type="button" class="btn btn-block btn-primary" id="placeReplenishmentOrder">
                    <spring:theme code="checkout.summary.scheduleReplenishment" />
                </button>
                <button type="button" class="btn btn-block btn-default" id="cancelReplenishmentOrder">
                    <spring:theme code="checkout.summary.replenishmentScheduleForm.cancel" />
                </button>
            </div>
        </div>
	</div>
</div>

