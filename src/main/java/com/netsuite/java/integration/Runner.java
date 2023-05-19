package com.netsuite.java.integration;

public class Runner {

    public static void main(String[] args) {
        String result = new Integration().makeAuthenticatedRequest(payload);
        System.out.println(result);
    }

    static String payload = """
            {
                "operation": "create",
                "values": [{
                        "custentity_sna_is_driver": false,
                        "entityid": "test dev5",
                        "companyname": "test three",
                        "firstname": "Three",
                        "lastname": "Doe",
                        "email": "ajohndoe2@example.com",
                        "primarysubsidiary": "1",
                        "custentity_sna_cus_market": "1",
                        "addresses": [{
                                "custrecord_sna_address_driverslicense": true,
                                "addr1": "Drivers",
                                "addr2": "Suite 1001",
                                "city": "Anytown",
                                "state": "CA",
                                "zip": "12345",
                                "country": "US"
                            },
                            {
                                "custrecord_sna_address_delivery": true,
                                "addr1": "Delivery",
                                "addr2": "Suite 2001",
                                "city": "Anytown",
                                "state": "CA",
                                "zip": "12345",
                                "country": "US"
                            }
                        ]
                    }
                ]
            }
            """;
}
