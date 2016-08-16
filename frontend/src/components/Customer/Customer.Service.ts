/// <reference path="../app/App.Constants.ts" />
/// <reference path="../app/App.Resource.ts" />
/// <reference path="../../typeDefinitions/Moment.d.ts" />
/// <reference path="../Customer/Customer.Model.ts" />
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

const CustomerServiceId: string = "CustomerService";

class CustomerService {

    private $inject = [customerResourceId];

    customerResource: any;
    customer: Array<Customer>;

    constructor(CustomerResource: CustomerResource) {
        this.customerResource = CustomerResource.resource;
        this.customer = new Array<Customer>();
        this.getAllCustomer();
    }

    saveCustomer(customer: Customer, insert: boolean) {
        let self = this;
        if (insert) {
            customer.timestamp = newTimestamp();
            this.customerResource.createCustomer(customer).$promise.then(function (result: Customer) {
                self.customer.push(result);
            });
        } else {
            this.customerResource.updateCustomer(customer).$promise.then(function (result: Customer) {
                customer = result;
            });
        }
    }
    getAllCustomer() {
        let self = this;
        this.customerResource.getAllCustomer().$promise.then(function (result: Array<Customer>) {
            console.log(result);
            self.customer = result;
        });
    }
}

angular.module(moduleCustomerService, [ngResourceId]).service(CustomerServiceId, CustomerService);