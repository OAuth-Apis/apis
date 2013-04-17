/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.surfnet.oaaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.surfnet.oaaas.model.AccessToken;
import org.surfnet.oaaas.model.Client;

@Repository
public interface AccessTokenRepository extends CrudRepository<AccessToken, Long> {

  AccessToken findByToken(String token);

  AccessToken findByRefreshToken(String refreshToken);

  List<AccessToken> findByResourceOwnerIdAndClient(String resourceOwnerId, Client client);

  List<AccessToken> findByResourceOwnerId(String resourceOwnerId);

  AccessToken findByIdAndResourceOwnerId(Long id, String owner);

  @Query(value = "select count(distinct resourceOwnerId) from accesstoken where client_id = ?1", nativeQuery = true)
  Number countByUniqueResourceOwnerIdAndClientId(long clientId);
}
