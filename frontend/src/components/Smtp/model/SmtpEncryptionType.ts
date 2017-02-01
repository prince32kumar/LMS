enum SmtpEncryptionType {
    SSL = <any>"SSL",
    TLS = <any>"TLS",
    STARTTLS = <any>"STARTTLS",

    getAll = <any>function (): Array<SmtpEncryptionType> {
        return Object.keys(SmtpEncryptionType).filter(f => f !== "getAll").map(k => SmtpEncryptionType[k]).filter(f => f !== "getAll");
    }
}