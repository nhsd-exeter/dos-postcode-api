The postcode insert and extract lambdas set up elastic networking interfaces (ENIs) automatically.

When these are created, they are created without tags, although tags can be set for them.

When attempting to tear-down infrastructure, due to IAM permissions, the ENIs cannot be deleted,
because they do not have the correct tags.

Until this is fixed, the ENIs must be deleted manually.

In order to delete them manually, you will first see the errors:

```
Error: error deleting Lambda ENIs using Security Group (sg-04b0274ddcaf01cab): error deleting Lambda ENI (eni-0d56826047a6b8d81):
  error deleting EC2 Network Interface (eni-0d56826047a6b8d81):
    UnauthorizedOperation: You are not authorized to perform this operation.
    Encoded authorization failure message:
    Ce6_MGJxjzZopaS98qIuc8FGzRZosvRriqi68Pq85aU4EN-nZcJDZwuMXuUql6HQ1xNcoaBDx4NJbZel-N3BxbnLl8UXPEJb8Bg8O8p
    TFbvwcFFIz2HMwX-kP_48KxbebwCKJ0-R78247P0raKVxq26ggB33a1Lm5WylnHpqcWGJti6xCyb3V_vVZlOLiccRoFRNdDCv2Z0rBC
    gk_t_RV0smIQB27RYGl8kmAoK634Q0n94lWYTyXZpCB3rAYX_b6orB1fovTKYXcz9n2YOo2XrbvSLRtu7ooxGUqhf-lBhTfIxammkuJ
    QKI1fHBtl69Z_9wZvAOT-rWHNpIytBfanaJRtFU_5w9DCaM08ME3l8m9BwHbmPPyd9GAKjQOCr09ttHGUZdCE4O-MvTYCVFI-z70fGV
    JFC27iSjyRbjl9qYpRHmQSIBVyYzuhBhZYtLckfoHzAugDlGCptEg068UGLEYdRFhGoZMA44tCDk9k_26BbaMajAE6bLFXzgnLbhiS
    4tEt8HMao4NVtjqUuFmfko4rl-j0FF0m-mTbdhuMUWmA8QyO17WkFMYIm3kZ1woLCKAq3A7Nj0phIQP4vGBNXyVDyl9zWDN11BiAZy
    pdGTXTBC8Ep_Dc7-crRVaWKGupOfGXVZ2ho3PGuCdbJraLKuYt3h20WnsOVg8k7kf2qNDUSrFKvFo6zZWOOixm6kucB6ie1Aif9QPi
    h4g8hcwSoEOrE
        status code: 403, request id: 7a2f107d-ee46-49ff-8038-dbce3c40299b
```

It isn't necessary, but you can if you wish, decode each of these messages using the following command:

```
aws sts decode-authorization-message --encoded-message "Ce6_MGJxjzZopaS98qIuc8F...whole encoded message"
```

This will give you a JSON object with a `DecodedMessage` field.  This field in turn is an escaped JSON
message, containing a `resource` attribute, such as `"arn:aws:ec2:eu-west-2:730319765130:network-interface/eni-0d56826047a6b8d81`

Your next task is to correctly tag the ENIs.  Take the eni ID from the original error message, and:

```
aws ec2 create-tags --resources eni-0a5fa4c50c026ff07eni-021c71ff3645d0237  --tags Key=Service,Value=service-finder
```
Now re-run the destroy

`make terraform-destroy PROFILE=dev`

You may need to re-run several times.
