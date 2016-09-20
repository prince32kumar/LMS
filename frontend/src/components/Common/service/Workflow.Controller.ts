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

    $inject = ["process", "$uibModalInstance", NotificationServiceId, TemplateServiceId, CustomerServiceId, ProductServiceId, WorkflowServiceId, LeadServiceId, OfferServiceId, SaleServiceId];

    uibModalInstance;

    editWorkflowUnit: Offer;
    template: Template = new Template();
    notification: Notification = new Notification();

    templates: Array<Template> = [];
    products: Array<Product> = [];

    customerService: CustomerService;
    productService: ProductService;
    notificationService: NotificationService;
    templateService: TemplateService;
    workflowService: WorkflowService;

    currentOrderPositions: Array<OrderPosition>;
    currentProductId = "-1";
    currentProductAmount = 1;
    currentCustomerId = "-1";
    customerSelected: boolean = false;

    editForm: any;
    editProcess: Process;
    edit: boolean;

    leadService: LeadService;
    offerService: OfferService;
    saleService: SaleService;

    currentTab: number = 1;
    currentTab1Class: any = "current";
    currentTab2Class: any = "done";
    currentTab3Class: any = "done";

    constructor(process, $uibModalInstance, NotificationService, TemplateService, CustomerService, ProductService, WorkflowService, LeadService, OfferService, SaleService) {
        super(WorkflowService);
        this.editWorkflowUnit = process.offer;
        this.uibModalInstance = $uibModalInstance;
        this.notificationService = NotificationService;
        this.templateService = TemplateService;
        this.customerService = CustomerService;
        this.productService = ProductService;
        this.workflowService = WorkflowService;

        this.getAllActiveTemplates();
        this.getAllActiveProducts();

        this.leadService = LeadService;
        this.offerService = OfferService;
        this.saleService = SaleService;

        this.loadDataToModal(process);
    }

    tabOnClick(tab: number) {
        this.currentTab = tab;
        if (this.currentTab === 1) {
            this.currentTab1Class = "current";
            this.currentTab2Class = "done";
            this.currentTab3Class = "done";
        } else if (this.currentTab === 2) {
            this.currentTab1Class = "done";
            this.currentTab2Class = "current";
            this.currentTab3Class = "done";
        } else if (this.currentTab === 3) {
            this.currentTab1Class = "done";
            this.currentTab2Class = "done";
            this.currentTab3Class = "current";
        }
    }

    loadDataToModal(process: Process) {
        this.edit = true;
        this.currentProductId = "-1";
        this.currentProductAmount = 1;
        this.editProcess = process;
        this.currentOrderPositions = deepCopy(this.editProcess.offer.orderPositions);
        this.customerSelected = this.editProcess.offer.customer.id > 0;
        this.currentCustomerId = this.editProcess.offer.customer.id + "";
        this.editWorkflowUnit = deepCopy(this.editProcess.offer);
    }

    ok() {
        this.uibModalInstance.close();
    }

    close() {
        this.uibModalInstance.close();
    }

    generate(templateId: string, offer: Offer) {
        this.templateService.generate(templateId, offer).then((result) => this.notification = result, (error) => console.log(error));
    }

    generatePDF(templateId: string, offer: Offer) {
        this.templateService.generatePDF(templateId, offer).then((result) => console.log(result), (error) => console.log(error));
    }

    getAllActiveTemplates() {
        this.templateService.getAll().then((result) => this.templates = result, (error) => console.log(error));
    }

    getAllActiveProducts() {
        this.productService.getAllProducts().then((result) => this.products = result, (error) => console.log(error));
    }

    getOrderPositions(process: Process): Array<OrderPosition> {
        if (!isNullOrUndefined(process.offer)) {
            return process.offer.orderPositions;
        }
    }

    send() {
        this.notificationService.send(this.notification);
    }

    selectCustomer(workflow: any) {
        this.customerSelected = this.workflowService.selectCustomer(workflow, this.currentCustomerId);
    }

}

angular.module(moduleWorkflow, ["summernote"]).service(WorkflowControllerId, WorkflowController);
