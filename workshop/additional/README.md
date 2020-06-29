# The JavaWebToken (JWT) format from Keycloak

To examine Java Web Token format you get from keycloak, you can use [JWT.io](https://jwt.io) to extract the content from a bearer token.

```json
{
  "exp": 1585571903,
  "iat": 1585571603,
  "jti": "f5cec896-d1b7-42d5-aaa1-c346daa41394",
  "iss": "http://localhost:8282/auth/realms/cloudnativestarter",
  "aud": "account",
  "sub": "69ac13eb-efe4-407d-9d2f-a07c50cfbb6f",
  "typ": "Bearer",
  "azp": "authors-client-cloud-native-starter",
  "session_state": "bedb8cab-7253-40a5-a64e-87a2bd0938d1",
  "acr": "1",
  "realm_access": {
    "roles": [
      "authors-role-cloud-native-starter"
    ]
  },
  "resource_access": {
    "account": {
      "roles": [
        "manage-account",
        "manage-account-links",
        "view-profile"
      ]
    }
  },
  "scope": "profile email",
  "email_verified": false,
  "groups": [
    "authors-role-cloud-native-starter"
  ],
  "preferred_username": "author-cloud-native-starter"
}
```