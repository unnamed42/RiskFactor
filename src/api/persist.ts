const auth = {
    get token() { return localStorage.getItem("auth.token") || ""; },
    set token(t: string) { localStorage.setItem("auth.token", t); },

    get expiry() {
        const expire = localStorage.getItem("auth.expiry");
        // if localStorage doesn't have expiry yet, then we are doing a
        // fresh login, which can't be expired
        return expire == null ? Number.MAX_VALUE : Number(expire);
    },
    set expiry(e: number) {
        // if(e instanceof Date) e = e.getTime();
        localStorage.setItem("auth.expiry", e.toString());
    },

    get username() { return localStorage.getItem("auth.username") || ""; },
    set username(u: string) { localStorage.setItem("auth.username", u); },

    clear() {
        localStorage.removeItem("auth.token");
        localStorage.removeItem("auth.expiry");
    }
};

export {
    auth
};
