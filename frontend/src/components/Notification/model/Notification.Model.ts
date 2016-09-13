/// <reference path="../../common/model/AbstractModel.Model.ts" />
/// <reference path="../../Customer/model/Customer.Model.ts" />
/// <reference path="../../File/model/File.Model.ts" />

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

class Notification extends AbstractModel {

    recipient: string;
    subject: string;
    content: string;
    attachment: File;

    constructor() {
        super();
    }
}
