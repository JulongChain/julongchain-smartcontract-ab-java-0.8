/*copyright Dingxuan. All Rights Reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import shim.ISmartContractStub;
import shim.SmartContractBase;

import java.util.List;


/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/05/26
 * @company Dingxuan
 */
public class MyCc extends SmartContractBase {

  private static Log log = LogFactory.getLog(MyCc.class);

  @Override
  public SmartContractResponse init(ISmartContractStub stub) {
    log.info("=================================================>>>> myCc init");
    List<byte[]> args = stub.getArgs();
    log.info(args.toString());
    log.info("args size:" + args.size());
    for (int i = 0; i < args.size(); i++) {
      System.out.println("args" + i + ": " + new String(args.get(i)));
    }
    stub.putState("a", "100".getBytes());
    stub.putState("b", "200".getBytes());
    stub.putState("c", "300".getBytes());
    return newSuccessResponse();
  }

  @Override
  public SmartContractResponse invoke(ISmartContractStub stub) {
    log.info("=================================================>>>> myCc invoke");
    List<byte[]> args1 = stub.getArgs();
    log.info("args1:" + args1.toString());
    log.info("args size:" + args1.size());

    final String function = stub.getFunction();
    final String[] args = stub.getParameters().toArray(new String[0]);

    log.info("function:" + function);
    stub.putState("a", "10".getBytes());
    byte[] bytes = stub.getState("b");
    if (bytes != null && bytes.length > 0) {
      int temp = Integer.parseInt(new String(bytes));
      stub.putState("b", ((temp - 1) + "").getBytes());
    }

    return newSuccessResponse();
  }

  @Override
  public String getSmartContractStrDescription() {
    return "MyCC";
  }
}
