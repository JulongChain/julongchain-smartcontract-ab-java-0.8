package smartcontract;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import shim.ISmartContractStub;
import shim.SmartContractBase;


import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

public class Demonstration extends SmartContractBase {

    private static Log log = LogFactory.getLog(Demonstration.class);

    @Override
    public SmartContractResponse init(ISmartContractStub stub) {
        log.info("===============================>>>> test init");
        try {
            final String function = stub.getFunction();
            final String[] args = stub.getParameters().toArray(new String[0]);

            log.info("------------------>>> function:"+function);

            if (args.length != 4) throw new IllegalArgumentException("Incorrect parameter format");
            for (int i = 0; i < args.length; i++) {
                System.out.println("args" + i + ": " + args[i]);
            }

            String accountOneName=args[0];
            String accountOneBalance=args[1];
            String accountTwoName=args[2];
            String accountTwoBalance=args[3];

            stub.putState(accountOneName, accountOneBalance.getBytes(StandardCharsets.UTF_8));
            stub.putState(accountTwoName, accountTwoBalance.getBytes(StandardCharsets.UTF_8));

        } catch (Throwable e) {
        return newErrorResponse(e);
    }
        return newSuccessResponse("InitSuccess");
    }

    @Override
    public SmartContractResponse invoke(ISmartContractStub stub) {
        log.info("================================>>>> test invoke");

        final String function = stub.getFunction();
        log.info("------------------>>> function:"+function);
        final String[] args = stub.getParameters().toArray(new String[0]);
        for(String s:args){log.info("------------------>>> args:"+s); }

        switch (function) {
            case "move":
                return move(stub, args);
            case "query":
                return query(stub, args);
            default:
                return newErrorResponse(format("unknown function: %s", function));
        }
    }

    @Override
    public String getSmartContractStrDescription() {
        return "Test";
    }

    // 转账
    private SmartContractResponse move(ISmartContractStub stub, String[] args) {
        log.info("================================>>>> test move");

        if (args.length != 3) throw new IllegalArgumentException("Parameter error");
        // 转账，Ａ转账给Ｂ，金额Ｃ
        final String fromKey = args[0]; // A
        final String toKey = args[1]; // B
        final String amount = args[2]; // C
        log.info("\n================================");
        log.info("转账人：" + fromKey + "收款人：" +toKey+"金额："+amount);
        // 获取身份信息
        final String fromKeyState = stub.getStringState(fromKey);
        final String toKeyState = stub.getStringState(toKey);
        log.info("\n================================");
        log.info(fromKey+":"+fromKeyState+"----------->>"+toKey+":"+toKeyState);
        if(fromKey.equals(toKey)){
            return newErrorResponse("Please do not transfer money to yourself.");
        }

        // 转账人余额获取类型转换
        BigDecimal fromAccountBalance = new BigDecimal(fromKeyState);
        BigDecimal toAccountBalance = new BigDecimal(toKeyState);

        // 转账金额类型转换
        BigDecimal transferAmount = new BigDecimal(amount);

        // 确保金额足够
        if (transferAmount.compareTo(fromAccountBalance) > 0) {
            return newErrorResponse("Lack of funds");
        }if (transferAmount.compareTo(BigDecimal.valueOf(0))<0){
            return newErrorResponse("The transfer amount must be greater than zero");
        }

        else{

            // 转账操作
            log.info("\n================================"+format("%s transfer %s to %s", fromKey, transferAmount.toString(), toKey));
            BigDecimal newFromAccountBalance = fromAccountBalance.subtract(transferAmount);
            BigDecimal newToAccountBalance = toAccountBalance.add(transferAmount);
            log.info("\n================================"+format("balance: %s = %s, %s = %s", fromKey, newFromAccountBalance.toString(), toKey, newToAccountBalance.toString()));

            stub.putState(fromKey, newFromAccountBalance.toString().getBytes(StandardCharsets.UTF_8));
            stub.putState(toKey, newToAccountBalance.toString().getBytes(StandardCharsets.UTF_8));

            return newSuccessResponse("Successful transfer "+transferAmount.toString());
        }
    }

    // 查询
    private SmartContractResponse query(ISmartContractStub stub, String[] args) {
        if (args.length != 1) throw new IllegalArgumentException("Parameter error");
        if (args[0].equals("")){
            return newErrorResponse("args[0]为空");
        }
        final String accountKey = args[0];
        BigDecimal amount = new BigDecimal(stub.getStringState(accountKey));
        String message ="[{\"name\":\""+accountKey+"\",\"value\":"+amount.toString()+"}]";
        return newSuccessResponse(message);
    }
}
