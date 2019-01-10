package test;

import smartcontract.Demonstration;
import org.junit.Test;
import shim.ISmartContract;
import shim.ISmartContractStub;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DemonstrationTest {
    private ISmartContractStub stub = mock(ISmartContractStub.class);

    @Test
    public void invokeMove() {
        String[] Init={"a","a","100.0"};
        List<String> list=Arrays.asList(Init);
        when(stub.getFunction()).thenReturn("move");
        when(stub.getParameters()).thenReturn(list);
        when(stub.getStringState("a")).thenReturn("500.0");
        when(stub.getStringState("b")).thenReturn("500.0");

        Demonstration t=new Demonstration();
        ISmartContract.SmartContractResponse smartContractResponse=t.invoke(stub);
//
        System.out.println(smartContractResponse.getMessage());
//        System.out.println(smartContractResponse.getStatus());
//        assertThat(smartContractResponse.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }


    @Test
    public void invokeQuery() {
        String[] Init={"a"};
        List<String> list=Arrays.asList(Init);

        when(stub.getFunction()).thenReturn("query");
        //when(stub.getFunction()).thenReturn("query");
        when(stub.getParameters()).thenReturn(list);
        when(stub.getStringState("a")).thenReturn("500.0");

        Demonstration t=new Demonstration();
        ISmartContract.SmartContractResponse smartContractResponse=t.invoke(stub);
//        String a="1";
//        try {
//            a = new String(smartContractResponse.getPayload(),"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        System.out.println(smartContractResponse.getMessage());
        assertThat(smartContractResponse.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }
}
