/*
 * Copyright 2019 TopicQuests
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.topicquests.asr.general;

import org.topicquests.asr.general.document.DocumentClient;
import org.topicquests.asr.general.document.api.IDocumentClient;
import org.topicquests.asr.general.tuple.TupleClient;
import org.topicquests.asr.general.tuple.api.ITupleClient;
import org.topicquests.asr.paragraph.ParagraphClient;
import org.topicquests.asr.paragraph.api.IParagraphClient;
import org.topicquests.asr.sentence.SentenceClient;
import org.topicquests.asr.sentence.api.ISentenceClient;
import org.topicquests.pg.PostgresConnectionFactory;
import org.topicquests.support.RootEnvironment;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class GeneralDatabaseEnvironment extends RootEnvironment {
  private PostgresConnectionFactory provider;
	private ISentenceClient sentenceClient;
	private IDocumentClient documentClient;
	private ITupleClient tupleClient;
	private IParagraphClient paragraphClient;
	
	/**
	 * Clients who use this must provide a <code>schemaName</code> which
	 * must already be present in PostgresQL
	 * @param schemaName 
	 */
	public GeneralDatabaseEnvironment(String schemaName) {
		super("postgress-props.xml");
		String dbName = getStringProperty("DatabaseName");
		System.out.println("DB: "+dbName);
        provider = new PostgresConnectionFactory(dbName, schemaName);
		sentenceClient = new SentenceClient(this, provider);
		documentClient = new DocumentClient(this, provider);
		tupleClient = new TupleClient(this, provider);
		paragraphClient = new ParagraphClient(this, provider);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				shutDown();
			}
		});
	}
	
	public IParagraphClient getParagraphClient () {
		return paragraphClient;
	}
	
	public ISentenceClient getSentenceClient() {
		return sentenceClient;
	}
	
	public IDocumentClient getDocumentClient() {
		return documentClient;
	}
	
	public ITupleClient getTupleClient() {
		return tupleClient;
	}
	
	public void clearDatabase() {
		IResult x = sentenceClient.clearTable();
		x = documentClient.clearTable();
		x = tupleClient.clearTable();
		System.out.println("SentenceDatabaseEnvironment.clearDatabase "+x.getErrorString()+" "+x.getResultObject());
	}
	
	public void shutDown() {
		sentenceClient.shutDown();
		documentClient.shutDown();
		tupleClient.shutDown();
		paragraphClient.shutDown();
	}

}
