declare var jwt_decode;

const TokenServiceId: string = "TokenService";

// const ACCESS_TOKEN: string = "accessToken";
const REFRESH_TOKEN: string = "refreshToken";
const USER_STORAGE: string = "user";

class TokenService {

    $inject = [$qId, $rootScopeId, $locationId];

    private accessToken: string | null;
    private accessTokenJson: any;
    private refreshToken: string | null;
    private refreshTokenJson: any;
    private loggedIn: boolean = false;
    private initPromise: Promise<void>;
    private tokenRefreshPromise: Promise<string>;
    private tokenRefreshInProgress: boolean = false;
    private tenant: String;
    private localStorageMap: {} = {};

    constructor(private $q, private $rootScope, private $location) {
        this.setTenant();
        this.init();
    }

    private async init(): Promise<void> {
        this.initPromise = this.initToken();
    }

    private setTenant(): void {
        let host = this.$location.host();
        let tempTenant: string = "";
        if (host.indexOf(".") < 0) {
            tempTenant = null;
        } else if (host.split(".")[0] === "www") {
            tempTenant = host.split(".")[1];
        } else {
            tempTenant = host.split(".")[0];
        }
        this.tenant = tempTenant === "leadplus" || tempTenant === "boexli" ? null : tempTenant;
    }

    private async initToken(): Promise<void> {
        console.log("init start", this.loggedIn);
        this.refreshToken = this.getItemFromLocalStorage(REFRESH_TOKEN);
        this.accessTokenJson = this.accessToken == null ? null : jwt_decode(this.accessToken);
        this.refreshTokenJson = this.refreshToken == null ? null : jwt_decode(this.refreshToken);
        if (this.isExpired(this.refreshTokenJson)) {
            this.loggedIn = false;
            console.log("refreshToken expired");
            this.logout(false);
        } else if (!this.isExpired(this.accessTokenJson)) {
            this.loggedIn = true;
        } else {
            console.log("accessToken expired");
            this.accessToken = await this.getAccessTokenPromise();
            this.accessTokenJson = jwt_decode(this.accessToken);
            this.loggedIn = true;
        }

    }

    public setToken(newToken: { token: string, refreshToken: string }): void {
        this.accessToken = newToken.token;
        this.refreshToken = newToken.refreshToken;
        this.accessTokenJson = this.accessToken == null ? null : jwt_decode(this.accessToken);
        this.refreshTokenJson = this.refreshToken == null ? null : jwt_decode(this.refreshToken);
        this.saveItemToLocalStorage(REFRESH_TOKEN, this.refreshToken);
        console.log(newToken);
        this.loggedIn = true;
    }

    private async getAccessTokenByRefreshToken(refreshToken: string): Promise<{ token: string }> {
        let self = this;
        let defer = this.$q.defer();
        $.ajax({
            type: "GET",
            url: "/api/rest/auth/token",
            headers: { "X-Authorization": "Bearer " + refreshToken },
            success: (response: { token: string, refreshToken: string }) => {
                defer.resolve(response);
            },
            error: (error) => {
                defer.reject(error);
            }
        });
        return defer.promise;
    }

    public setTokenByCredentials(credentials: { username: string, password: string }): Promise<void> {
        let self = this;
        let defer = this.$q.defer();
        console.log(credentials);
        $.ajax({
            type: "POST",
            url: "/api/rest/auth/login",
            data: JSON.stringify(credentials),
            contentType: "application/json; charset=utf-8",
            success: (response: { token: string, refreshToken: string }) => {
                self.setToken(response);
                defer.resolve();
            },
            error: (error) => {
                self.loggedIn = false;
                console.log("setTokenByCredentials", error);
                defer.reject(error);
            }
        });
        return defer.promise;
    }

    public async getAccessTokenPromise(): Promise<string> {
        if (this.tokenRefreshInProgress === true) { return this.tokenRefreshPromise; }
        this.tokenRefreshInProgress = true;
        this.tokenRefreshPromise = this.getAccessToken();
        let temp = await this.tokenRefreshPromise;
        this.tokenRefreshInProgress = false;
        return temp;

    }

    private async getAccessToken(): Promise<string> {
        try {
            if (this.accessToken != null && !this.isExpired(this.accessTokenJson)) { return this.accessToken; }
            if (this.refreshToken == null || this.isExpired(this.refreshTokenJson)) { throw Error("Refresh token expired!"); }

            console.log("refresh");
            let temp: { token: string } = await this.getAccessTokenByRefreshToken(this.refreshToken);
            console.log(temp);
            this.accessToken = temp.token;
            this.accessTokenJson = jwt_decode(this.accessToken);
            console.log(this.accessToken);
            this.loggedIn = true;
            return this.accessToken;

        } catch (error) {
            this.loggedIn = false;
            console.log("getAccessToken", error);
            this.logout();
        }
    }

    public getAccessTokenInstant(): string {
        try {
            if (this.accessToken != null && !this.isExpired(this.accessTokenJson)) { return this.accessToken; }
            if (this.refreshToken == null || this.isExpired(this.refreshTokenJson)) { throw Error("Refresh token expired!"); }

            this.refreshAccessToken();
            return this.refreshToken;

        } catch (error) {
            this.loggedIn = false;
            console.log("getAccessToken", error);
            this.logout();
        }

    }
    private async refreshAccessToken(): Promise<void> {
        try {
            let temp = await this.getAccessTokenByRefreshToken(this.refreshToken);
            this.accessToken = temp.token;
            this.accessTokenJson = jwt_decode(this.accessToken);
            this.loggedIn = true;
        } catch (error) {
            this.loggedIn = false;
            console.log("refreshAccessToken", error);
            this.logout();
        }
    }


    private isExpired(token: any): boolean {
        return token == null || token.exp * 1000 < new Date().getTime();
    }

    public logout(fullPageReload: boolean = true) {
        console.log("logout");
        this.loggedIn = false;
        this.removeAllItemsFromLocalStorage();
        // this.$rootScope.user = undefined;
        let port = this.$location.port();
        port = ":" + port;
        if (port !== ":8080") {
            port = "";
        }
        alert();
        /*
        console.log(this.$location.path());
        if (fullPageReload === true) {
            window.open("https://" + this.$location.host() + port + "/", "_self");
        } else if (fullPageReload === false) {
            this.$location.path("/login");
        }
        console.log("exit");
        */
    }

    public isLoggedIn(): boolean {
        console.log(this.loggedIn);
        return this.loggedIn;
    }

    public async awaitInit(): Promise<void> {
        await this.initPromise;
    }

    public getSmtpKey(): string {
        console.log(this.accessTokenJson.signature);
        return this.accessTokenJson.signature;
    }

    public saveItemToLocalStorage(key: string, value: any): void {
        if (key == null || value == null) { return; }
        this.localStorageMap[key] = null;
        localStorage.setItem(this.tenant + "_" + key, JSON.stringify(value));
    }

    public getItemFromLocalStorage(key: string): any {
        if (key == null) { return null; }
        let temp: string = localStorage.getItem(this.tenant + "_" + key);
        if (temp == null) { return null; }
        return JSON.parse(temp);
    }

    public removeItemFromLocalStorage(key: string): void {
        if (key == null) { return; }
        localStorage.removeItem(this.tenant + "_" + key);
    }

    private removeAllItemsFromLocalStorage(): void {
        console.log(this.localStorageMap);
        for (let key in this.localStorageMap) {
            console.log(key);
            localStorage.removeItem(this.tenant + "_" + key);
        }
        localStorage.removeItem(this.tenant + "_" + USER_STORAGE);
        localStorage.removeItem(this.tenant + "_" + REFRESH_TOKEN);
    }


}
angular.module(moduleTokenService, []).service(TokenServiceId, TokenService);