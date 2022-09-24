/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.oauth2.server.exception;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/4/23 0023 上午 11:44
 */
@Data
@AllArgsConstructor
public class RenHttpResult {

    private String code;

    private String msg;

}
