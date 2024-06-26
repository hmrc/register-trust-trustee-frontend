
# Register Trust Trustee Frontend

This service is responsible for collecting details of the trustees and lead trustee of the trust when registering a trust.

To run locally using the micro-service provided by the service manager:

```bash
sm2 --start TRUSTS_ALL
```

or

```bash
sm2 --start REGISTER_TRUST_ALL
```

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 8839 but is defaulted to that in build.sbt):

```bash
sbt run
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
