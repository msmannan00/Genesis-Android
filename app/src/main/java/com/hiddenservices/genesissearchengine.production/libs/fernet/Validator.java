/**
   Copyright 2017 Carlos Macasaet

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.hiddenservices.genesissearchengine.production.libs.fernet;


/**
 * This class validates a token according to the Fernet specification. It may be extended to provide domain-specific
 * validation of the decrypted content of the token. If you use a dependency injection / inversion of control framework,
 * it would be appropriate for a subclass to be a singleton which accesses a data store.
 *
 * <p>Copyright &copy; 2017 Carlos Macasaet.</p>
 *
 * @param <T>
 *            The type of the payload. The Fernet token encodes the payload in binary. The type T should be a domain
 *            object or data transfer object representation of that data.
 * @see StringObjectValidator
 * @see StringValidator
 * @author Carlos Macasaet
 */
public interface Validator<T> {



}