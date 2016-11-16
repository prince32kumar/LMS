/// <reference path="../../Offer/model/Offer.Model.ts" />
/// <reference path="../../app/App.Constants.ts" />
/// <reference path="../../app/App.Resource.ts" />
/// <reference path="../../Template/model/Template.Model.ts" />
/// <reference path="../../Notification/model/Notification.Model.ts" />
/// <reference path="../../Notification/controller/Notification.Service.ts" />
/// <reference path="../../Customer/controller/Customer.Service.ts" />
/// <reference path="../../Product/controller/Product.Service.ts" />
/// <reference path="../../Template/controller/Template.Service.ts" />
/// <reference path="../../Common/service/Workflow.Service.ts" />
/// <reference path="../../Common/model/Process.Model.ts" />
/// <reference path="../../FileUpload/controller/File.Service.ts" />

/*******************************************************************************
 * Copyright (c) 2016 Eviarc GmbH. All rights reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of
 * Eviarc GmbH and its suppliers, if any. The intellectual and technical
 * concepts contained herein are proprietary to Eviarc GmbH, and are protected
 * by trade secret or copyright law. Dissemination of this information or
 * reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Eviarc GmbH.
 ******************************************************************************/
"use strict";

const WorkflowControllerId: string = "WorkflowController";

class WorkflowController extends AbstractWorkflow {

    $inject = ["process", "$uibModalInstance", NotificationServiceId, TemplateServiceId, CustomerServiceId, ProductServiceId,
        WorkflowServiceId, LeadServiceId, OfferServiceId, SaleServiceId, DashboardServiceId, FileServiceId, $rootScopeId, $sceId, $windowId, $scopeId];

    type: string;

    uibModalInstance;

    editWorkflowUnit: any;
    process: Process;
    template: Template = new Template();

    templates: Array<Template> = [];
    products: Array<Product> = [];

    customerService: CustomerService;
    notificationService: NotificationService;
    productService: ProductService;
    templateService: TemplateService;
    workflowService: WorkflowService;
    dashboardService: DashboardService;
    fileService: FileService;

    currentOrderPositions: Array<OrderPosition>;
    currentProductId = "-1";
    currentProductAmount = 1;
    currentNotification: Notification;

    editProcess: Process;
    edit: boolean;
    editEmail: boolean;
    editable: boolean;

    leadService: LeadService;
    offerService: OfferService;
    saleService: SaleService;

    customerEditForm: any;
    leadEditForm: any;
    priceEditForm: any;
    supplyEditForm: any;
    emailEditForm: any;
    saleEditForm: any;
    rootScope;
    scope;

    invoiceNumberAlreadyExists: boolean = false;

    window;

    constructor(process, type, $uibModalInstance, NotificationService, TemplateService, CustomerService, ProductService,
        WorkflowService, LeadService, OfferService, SaleService, DashboardService, FileService, $rootScope, $sce, $window, $scope) {
        super(WorkflowService, $sce, FileService, $scope);
        let self = this;
        this.rootScope = $rootScope;
        this.scope = $scope;
        this.process = process;
        this.type = type;
        if (this.type === "offer") {
            this.editWorkflowUnit = new Offer();
            this.editWorkflowUnit = this.process.offer;
            this.currentNotification = new Notification();
            this.currentNotification.recipient = this.editWorkflowUnit.customer.email;
            this.editEmail = false;
            this.editable = true;
        }
        else if (this.type === "sale") {
            this.wizardOnClick(6);
            this.editWorkflowUnit = new Sale();
            this.editWorkflowUnit = this.process.sale;
            this.editEmail = true;
            this.editable = false;
        }
        this.uibModalInstance = $uibModalInstance;
        this.notificationService = NotificationService;

        this.templateService = TemplateService;

        this.customerService = CustomerService;
        this.productService = ProductService;
        this.workflowService = WorkflowService;
        this.dashboardService = DashboardService;
        this.fileService = FileService;

        this.getAllActiveTemplates();
        this.getAllActiveProducts();

        this.leadService = LeadService;
        this.offerService = OfferService;
        this.saleService = SaleService;

        this.loadDataToModal(process);

        this.window = $window;
    }

    loadDataToModal(process: Process) {
        if (!isNullOrUndefined(this.customerEditForm)) {
            this.customerEditForm.$setPristine();
        }
        if (!isNullOrUndefined(this.leadEditForm)) {
            this.leadEditForm.$setPristine();
        }
        if (!isNullOrUndefined(this.supplyEditForm)) {
            this.supplyEditForm.$setPristine();
        }
        if (!isNullOrUndefined(this.priceEditForm)) {
            this.priceEditForm.$setPristine();
        }
        if (!isNullOrUndefined(this.emailEditForm)) {
            this.emailEditForm.$setPristine();
        }
        if (!isNullOrUndefined(this.saleEditForm)) {
            this.saleEditForm.$setPristine();
        }

        this.edit = true;
        this.currentProductId = "-1";
        this.currentProductAmount = 1;
        this.editProcess = process;

        if (this.type === "offer") {
            this.currentOrderPositions = deepCopy(this.editProcess.offer.orderPositions);
            this.customerSelected = this.editProcess.offer.customer.id > 0;
            this.selectedCustomer = this.editProcess.offer.customer;
            this.editWorkflowUnit = deepCopy(this.editProcess.offer);
        } else if (this.type === "sale") {
            this.currentOrderPositions = deepCopy(this.editProcess.sale.orderPositions);
            this.customerSelected = this.editProcess.sale.customer.id > 0;
            this.selectedCustomer = this.editProcess.sale.customer;
            this.editWorkflowUnit = deepCopy(this.editProcess.sale);
        }
    }

    close(result: boolean) {
        this.uibModalInstance.close(result);
        if (!result && (this.editProcess.status === Status.OPEN || this.editProcess.status === Status.INCONTACT)) {
            this.editProcess.offer = undefined;
        } else if (!result && (this.editProcess.status === Status.OFFER || this.editProcess.status === Status.FOLLOWUP)) {
            this.editProcess.sale = undefined;
        }
    }

    calculateProfit() {
        this.editWorkflowUnit.saleProfit = this.editWorkflowUnit.saleTurnover - this.editWorkflowUnit.saleCost;
    }

    generate(templateId: string, offer: Offer) {
        if (Number(templateId) === -1) {
            this.currentNotification.content = "";
            this.currentNotification.id = null;
        } else {
            offer.orderPositions = this.currentOrderPositions;
            this.templateService.generate(templateId, offer, this.currentNotification).then((result) => this.currentNotification = result, (error) => handleError(error));
        }
    }

    generatePDF(templateId: string, offer: Offer) {
        this.templateService.generatePDF(templateId, offer).then((result) => { }, (error) => handleError(error));
    }

    openAttachment(id: number) {
        this.fileService.getContentFileById(id);
    }

    getAllActiveTemplates() {
        this.templateService.getAll().then((result) => this.templates = result, (error) => handleError(error));
    }

    getAllActiveProducts() {
        this.productService.getAllProducts().then((result) => this.products = result, (error) => handleError(error));
    }

    getOrderPositions(process: Process): Array<OrderPosition> {
        if (!isNullOrUndefined(process.offer)) {
            return process.offer.orderPositions;
        }
    }

    existsInvoiceNumber() {
        let self = this;
        this.saleService.getSaleByInvoiceNumber(this.editWorkflowUnit.invoiceNumber).then(function (result: Sale) {
            if (!isNullOrUndefined(result)) {
                self.invoiceNumberAlreadyExists = true;
            } else {
                self.invoiceNumberAlreadyExists = false;
            }
        });
    }

    send() {
        let self = this;
        this.process.offer = this.editWorkflowUnit;
        let process = this.process;
        this.currentNotification.notificationType = NotificationType.OFFER;
        let notification = this.currentNotification;
        this.notificationService.sendNotification(notification).then(() => {
            self.workflowService.addLeadToOffer(process).then(function (tmpprocess: Process) {
                self.notificationService.saveAttachments(notification.attachments).then((result) => {
                    notification.attachments = result;
                    if (isNullOrUndefined(process.notifications)) {
                        process.notifications = [];
                    }
                    process.notifications.push(notification);
                    self.workflowService.saveProcess(process).then((resultProcess) => {
                        self.process.notifications = resultProcess.notifications;
                        self.close(true);
                    });
                });

            });
        });
    }

    save() {
        if (this.type === "offer") {
            let self = this;
            this.process.offer = this.editWorkflowUnit;
            let process = this.process;
            this.workflowService.addLeadToOffer(process).then(function (tmpprocess: Process) {
                self.rootScope.$broadcast("deleteRow", tmpprocess);
                self.close(true);
            });
        } else if (this.type === "sale") {
            this.process.sale = this.editWorkflowUnit;
            let self = this;
            this.workflowService.addOfferToSale(this.process).then(function (tmpprocess: Process) {
                self.rootScope.$broadcast("deleteRow", self.process);
                self.close(true);
            });
        }
    }

    getTheFiles($files) {
        let self = this;
        this.notificationService.setAttachmentToNotification($files, this.currentNotification).then(() => {
            try {
                self.scope.$digest();
            } catch (error) {
                handleError(error);
            }
        });
    }

    setFormerNotification(notificationId: number) {
        if (Number(notificationId) === -1) {
            this.currentNotification = new Notification();
        }
        let notification: Notification = findElementById(this.editProcess.notifications, Number(notificationId)) as Notification;
        if (!isNullOrUndefined(notification)) {
            this.currentNotification = deepCopy(notification);
            this.currentNotification.id = null;
        }
    }

}

angular.module(moduleWorkflow, [moduleSummernote]).service(WorkflowControllerId, WorkflowController);
