/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.laokou.auth.server.infrastructure.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author laokou
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "认证实体类",description = "认证实体类")
public class AuthToken {
    @Schema(name = "认证token",description = "认证token",example = "eyJ4NXQjUzI1NiI6IkZiN3FSd08yM1JjY1JSTkI0Z01sbEFla21GM3BhTTR5QnFyNVFfQ0lrSEEiLCJraWQiOiJhdXRoIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJhZG1pbiIsImF1ZCI6ImF1dGgtY2xpZW50IiwibmJmIjoxNjcyNTcyMzUzLCJzY29wZSI6WyJhZGRyZXNzIiwiYXV0aCIsInBob25lIiwib3BlbmlkIiwicHJvZmlsZSIsImVtYWlsIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6MTExMSIsImV4cCI6MTY3MjU3NTk1MywiaWF0IjoxNjcyNTcyMzUzfQ.GtajOqYBkrCt89WvFbPbskMxph7cMpzCRsB6taJbKJyOxG6aypNgG5gRS6ajsv9pCFX2FE_bAPzTGRBYz3rM_SpvZwbsrVaVaqsB5yOVrQ75lP8Jdgd5HHoUI3O9Mb3I_wlckSO1VgTr2rQ5Kj46RYFlZGTZQDLByCMknHer6XtF2NFIYoFtd2jtLQGH8uXoe1PmCpbt5J-VnxeiCjqknA8LWNB_86VveiHPVY9BAHUFdojKMTroWXE9QdELwayKKQIafBBEnMoEmJaCDXkqW1ieV9RFSBDJYGFWDG_NXeBGjLQFi2o_P8xjrrBPlWmNgD3Vs1ik2V9KRMHmTUEMsg")
    private String accessToken;
    @Schema(name = "刷新token",description = "刷新token",example = "bB7xtWE7CzECrLUpHgnQaflu9VCq-HeY43z4Yxvn5oWuQRTkITJb0LY_d9zhv1jwlSaV20yvsVwAT7YSG1peRw1RvjghGtFveCr9lZ9x0SqUFuPb03W550wR9jDyA-Fl")
    private String refreshToken;
    @Schema(name = "token类型",description = "token类型",example = "Bearer")
    private String tokenType;
    @Schema(name = "token有效时间",description = "token有效时间",example = "3600")
    private Long expiresIn;
}
