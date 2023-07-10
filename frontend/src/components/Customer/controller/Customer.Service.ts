/// <reference path="../../app/App.Constants.ts" />
/// <reference path="../../app/App.Resource.ts" />
/// <reference path="../../../typeDefinitions/Moment.d.ts" />
/// <reference path="../../Customer/model/Customer.Model.ts" />
"use strict";

const CustomerServiceId: string = "CustomerService";

class CustomerService {

    private $inject = [CustomerResourceId, $httpId, toasterId, $translateId, $rootScopeId];

    customerResource: any;
    pagingCustomers: Array<Customer> = new Array<Customer>();
    searchCustomers: Array<Customer> = new Array<Customer>();
    page: any;
    http: any;
    toaster: any;
    translate: any;
    rootScope: any;
    inconsistency: string;

    constructor(CustomerResource: CustomerResource, $http, toaster, $translate, $rootScope) {
        this.customerResource = CustomerResource.resource;
        this.http = $http;
        this.toaster = toaster;
        this.translate = $translate;
        this.rootScope = $rootScope;
        // this.getAllCustomerByPage(1, 20, "noSearchText", false);
    }

    async saveCustomer(customer: Customer, insert: boolean = false, upgradeToRealCustomer: boolean = false): Promise<Customer> {
        if (insert) {
            customer.timestamp = newTimestamp();
        }
        if (upgradeToRealCustomer) {
            customer.realCustomer = true;
        }
        try {
            let savedCustomer = await this.customerResource.createCustomer(customer).$promise;
            this.inconsistency = null;
            return savedCustomer;
        } catch (error) {
            throw error;
        }
    }

    async getCustomerById(customerId: number) {
        let customer = await this.customerResource.getCustomerById({ id: customerId }).$promise as Customer;
        if (isNullOrUndefined(customer.id)) {
            return null;
        }
        return customer;
    }

    // TODO change to mapzen api
    matchAddressCompoenents(addressCompoenentArray: Array<any>, address: Address) {
        if (isNullOrUndefined(addressCompoenentArray)) {
            return;
        }
        address.street = "";
        address.number = "";
        address.city = "";
        address.state = "";
        address.country = "";
        address.zip = "";
        for (let addressComponent of addressCompoenentArray) {
            if (!isNullOrUndefined(addressComponent.types)) {
                for (let type of addressComponent.types) {
                    if (type === "postal_code") {
                        address.zip = addressComponent.long_name;
                    } else if (type === "country") {
                        address.country = addressComponent.long_name;
                    } else if (type === "administrative_area_level_1") {
                        address.state = addressComponent.long_name;
                    } else if (type === "locality") {
                        address.city = addressComponent.long_name;
                    } else if (type === "route") {
                        address.street = addressComponent.long_name;
                    } else if (type === "street_number") {
                        address.number = addressComponent.long_name;
                    }
                }
            }
        }
    }

    getAddressLine(address: Address) {
        if (isNullOrUndefined(address)) {
            return "";
        }
        let addressStr: string = "";
        if (!isNullOrUndefined(address.street) && address.street !== "") {
            addressStr += address.street;
            if (!isNullOrUndefined(address.number)) {
                addressStr += " " + address.number;
            }
            addressStr += ", ";
        }
        if (!isNullOrUndefined(address.city) && address.city !== "") {
            if (!isNullOrUndefined(address.zip)) {
                addressStr += address.zip + " ";
            }
            addressStr += address.city;
            addressStr += ", ";
        }
        if (!isNullOrUndefined(address.state) && address.state !== "") {
            addressStr += address.state;
            addressStr += ", ";
        }
        if (!isNullOrUndefined(address.country)) {
            addressStr += address.country;
        }
        if (addressStr.endsWith(", ")) {
            addressStr = addressStr.slice(0, -2);
        }
        return addressStr;
    }

    getAllCustomerByPage(start: number, length: number, searchtext: string, allCustomers: boolean) {
        let self = this;
        this.customerResource.getAllCustomerByPage({ start: start, length: length, searchtext: searchtext, allCustomers: allCustomers }).$promise.then(function (result: any) {
            self.page = result;
            for (let customer of result.content) {
                self.pagingCustomers.push(customer);
            }
        });
    }


    getCustomerBySearchText(searchtext: string): any {
        if (!isNullOrUndefined(searchtext) && searchtext.length > 0) {
            let self = this;
            return this.http.get("/api/rest/customer/search/" + searchtext).then(function (response) {
                let temp: Array<Customer> = new Array<Customer>();
                for (let customer of response.data) {
                    if (customer.deactivated === false) {
                        self.searchCustomers.push(customer);
                        temp.push(customer);
                    }
                }
                return temp;
            });
        }
    }
    async deleteCustomer(customer: Customer): Promise<void> {
        await this.customerResource.deleteCustomer({ id: customer.id }).$promise;
    }
}

angular.module(moduleCustomerService, [ngResourceId]).service(CustomerServiceId, CustomerService);