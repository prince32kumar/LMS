/// <reference path="../../app/App.Constants.ts" />
/// <reference path="../../app/App.Constants.ts" />
/// <reference path="../../Workflow/controller/Workflow.Service.ts" />
/// <reference path="../../Wizard/model/WizardButtonConfig.Model.ts" />
/// <reference path="../../Common/directive/Directive.Interface.ts" />
/// <reference path="../../Process/controller/Process.Service.ts" />
/// <reference path="../../../typeDefinitions/angular.d.ts" />
/*******************************************************************************
 * Copyright (c) 2016 Eviarc GmbH.
 * All rights reserved.  
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Eviarc GmbH and its suppliers, if any.  
 * The intellectual and technical concepts contained
 * herein are proprietary to Eviarc GmbH,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Eviarc GmbH.
 *******************************************************************************/


const WizardDirectiveId: string = "wizard";

class WizardDirective implements IDirective {
    templateUrl = () => { return "components/Wizard/view/Wizard.html"; };
    transclude = {
        "customerEdit": "?customerEdit",
        "productEdit": "?productEdit",
        "customerProductEdit": "?customerProductEdit",
        "emailEdit": "?emailEdit",
        "saleEdit": "?saleEdit"
    };

    restrict = "E";
    scope = {
        modalTitle: "@",
        editProcess: "=",
        editWorkflowUnit: "=",
        modalInstance: "=",
        wizardConfig: "=",
        currentNotification: "=",
        transform: "<"
    };

    constructor(private WorkflowService: WorkflowService, private CustomerService: CustomerService, private ProcessService: ProcessService, private FileService: FileService, private NotificationService: NotificationService, private $rootScope) {
    }

    static directiveFactory(): WizardDirective {
        let directive: any = (WorkflowService: WorkflowService, CustomerService: CustomerService, ProcessService: ProcessService, FileService: FileService, NotificationService: NotificationService, $rootScope) => new WizardDirective(WorkflowService, CustomerService, ProcessService, FileService, NotificationService, $rootScope);
        directive.$inject = [WorkflowServiceId, CustomerServiceId, ProcessServiceId, FileServiceId, NotificationServiceId, $rootScopeId];
        return directive;
    }

    link(scope, element, attrs, ctrl, transclude): void {
        scope.workflowService = this.WorkflowService;
        scope.customerService = this.CustomerService;
        scope.processService = this.ProcessService;
        scope.fileService = this.FileService;
        scope.notificationService = this.NotificationService;
        scope.rootScope = this.$rootScope;
        scope.wizardElements = new Array<WizardButtonConfig>();
        scope.step = 1;
        scope.currentWizard;
        console.log(scope.transform);
        if (scope.transform === true) {
            console.log("Bin true");
        }

        if (!isNullOrUndefined(scope.currentNotification)) {
            scope.currentNotification.recipients = scope.editWorkflowUnit.customer.email;
            scope.$watch("editWorkflowUnit.customer.email", function (newValue, oldValue) {
                if (newValue !== oldValue && !isNullOrUndefined(scope.editWorkflowUnit)) {
                    scope.currentNotification.recipients = scope.editWorkflowUnit.customer.email;
                }
            }, true);
        }

        scope.close = (result: boolean, process: Process) => this.close(result, process, scope);
        scope.transformWorkflow = () => this.transformWorkflow(scope);
        scope.save = () => this.save(scope);
        scope.saveOrTransform = () => this.saveOrTransform(scope);
        scope.send = () => this.send(scope);
        scope.isAnyFormInvalid = () => this.isAnyFormInvalid(scope);
        scope.getNotificationType = () => this.getNotificationType(scope);
        scope.followUp = (process: Process) => this.followUp(process, scope);
        scope.isLead = () => this.isLead(scope);
        scope.isOffer = () => this.isOffer(scope);
        scope.isSale = () => this.isSale(scope);
        scope.isInOfferTransformation = () => this.isInOfferTransformation(scope);
        scope.isInSaleTransformation = () => this.isInSaleTransformation(scope);
        scope.getWizardConfigByTransclusion = (wizardConfig: Array<WizardButtonConfig>, transclusion: any) => this.getWizardConfigByTransclusion(wizardConfig, transclusion);
        let wizardConfig: Array<WizardButtonConfig> = scope.wizardConfig;
        let firstActiveElement = null;

        for (let transclusion in this.transclude) {
            transclude(function (content) {
                let wizardButtonConfig: WizardButtonConfig = scope.getWizardConfigByTransclusion(wizardConfig, transclusion);
                if (!isNullOrUndefined(wizardButtonConfig)) {
                    scope.wizardElements.push(wizardButtonConfig);
                    if (wizardButtonConfig.isFirstActiveElement) {
                        firstActiveElement = wizardButtonConfig;
                        scope.step = scope.wizardElements.indexOf(wizardButtonConfig) + 1;
                    }
                }

            }, null, transclusion);
        }
        isNullOrUndefined(firstActiveElement) ? scope.currentWizard = scope.wizardElements[0] : scope.currentWizard = firstActiveElement;
    };

    getWizardConfigByTransclusion(wizardConfig: Array<WizardButtonConfig>, transclusion: any): WizardButtonConfig {
        for (let buttonConfig of wizardConfig) {
            if (buttonConfig.directiveType === transclusion) {
                return buttonConfig;
            }
        }
        return null;
    }

    close(result: boolean, process: Process, scope: any) {
        console.log(process);
        scope.modalInstance.close(process);
        if (!result && scope.isLead()) {
            scope.editProcess.offer = undefined;
        } else if (!result && scope.isOffer()) {
            scope.editProcess.sale = undefined;
        }
    }

    saveOrTransform(scope: any) {
        console.log(scope.transform);
        if (scope.transform) {
            scope.transformWorkflow();
        }
        else {
            scope.save();
        }
    }

    async transformWorkflow(scope: any) {
        console.log("I Can transform ya");
        let process = scope.editProcess;
        let resultProcess = null;
        if (scope.isLead()) {
            resultProcess = await scope.workflowService.addLeadToOffer(process).catch(error => handleError(error));
        } else if (scope.isOffer()) {
            resultProcess = await scope.workflowService.addOfferToSale(process).catch(error => handleError(error));
        }
        scope.close(true, resultProcess);
    }

    async save(scope: any): Promise<Process> {
        let isNewProcess: boolean = isNullOrUndefined(scope.editProcess.id);
        let resultProcess = await scope.processService.save(scope.editProcess, scope.editWorkflowUnit, !isNewProcess, false).catch(error => handleError(error)) as Process;
        scope.close(true, resultProcess);
        return resultProcess;
    }

    getNotificationType(scope: any): NotificationType {
        if (scope.transform && scope.isInOfferTransformation()) {
            return NotificationType.OFFER;
        } else if (scope.transform && scope.isInSaleTransformation()) {
            return NotificationType.SALE;
        } else if (!scope.transform && scope.isLead()) {
            return NotificationType.LEAD;
        } else if (!scope.transform && scope.isOffer() && !scope.currentWizard.isFollowUp) {
            return NotificationType.OFFER;
        } else if (!scope.transform && scope.isOffer() && scope.currentWizard.isFollowUp) {
            return NotificationType.FOLLOWUP;
        } else if (!scope.transform && scope.isSale()) {
            return NotificationType.SALE;
        }
    }

    async send(scope: any) {
        let notificationType: NotificationType = scope.getNotificationType();
        let process: Process = scope.editProcess;
        process.notifications = process.notifications ? process.notifications : [];

        let notification: Notification = deepCopy(scope.currentNotification);
        notification.attachments = notification.attachments ? notification.attachments : [];
        notification.notificationType = notificationType;
        notification.id = undefined;
        let deleteRow = false;
        if (scope.isInOfferTransformation()) {
            deleteRow = true;
            console.log("ADD LEAD TO OFFER");
            await scope.workflowService.addLeadToOffer(process);
        } else if (scope.isInSaleTransformation()) {
            deleteRow = true;
            console.log("ADD OFFER TO SALE");
            await scope.workflowService.addOfferToSale(process);
        }
        let promises: Array<Promise<void>> = notification.attachments ?
            notification.attachments
                .filter(a => isNullOrUndefined(a.id))
                .map(a => scope.fileService.saveAttachment(a)) : [];
        for (let p of promises) {
            await p;
        }
        notification.attachments.forEach(a => a.id = undefined);
        process.notifications.push(notification);

        if (notificationType === NotificationType.FOLLOWUP) {
            if (process.status !== Status.FOLLOWUP && process.status !== Status.DONE) {
                process.status = Status.FOLLOWUP;
            }
        }
        console.log(process);
        let resultProcess = await scope.processService.save(process, scope.editWorkflowUnit, !deleteRow, deleteRow) as Process;
        scope.close(true, resultProcess);
        try {
            await scope.notificationService.sendNotification(notification);
        } catch (error) {
            // TODO Set Notification to Error            
            console.log("Set Notification to Error....");
            notification.notificationType = NotificationType.ERROR;
        }
    }

    async followUp(process, scope) {
        if (process.status !== Status.FOLLOWUP && process.status !== Status.DONE) {
            let resultProcess = await scope.processService.setStatus(process, Status.FOLLOWUP) as Process;
            scope.close(true, resultProcess);
        }
    }

    isLead(scope: any): boolean {
        return scope.workflowService.isLead(scope.editProcess);
    }

    isOffer(scope: any): boolean {
        return scope.workflowService.isOffer(scope.editProcess);
    }

    isSale(scope: any): boolean {
        return scope.workflowService.isSale(scope.editProcess);
    }

    isInOfferTransformation(scope: any): boolean {
        return scope.isLead() && !isNullOrUndefined(scope.editProcess.offer);
    }

    isInSaleTransformation(scope: any): boolean {
        return scope.isOffer() && !isNullOrUndefined(scope.editProcess.sale);
    }

    isAnyFormInvalid(scope: any): boolean {
        for (let buttonConfig of scope.wizardConfig) {
            if (!isNullOrUndefined(buttonConfig.form) && buttonConfig.form.$invalid && buttonConfig.validation) {
                return true;
            }
        }
        return false;
    }

}
angular.module(moduleApp).directive(WizardDirectiveId, WizardDirective.directiveFactory());

