/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.collector.stream.worker.base;

import org.skywalking.apm.collector.core.data.Data;
import org.skywalking.apm.collector.queue.base.QueueEventHandler;
import org.skywalking.apm.collector.queue.base.QueueExecutor;
import org.skywalking.apm.collector.queue.service.QueueCreatorService;
import org.skywalking.apm.collector.storage.service.DAOService;

/**
 * @author peng-yongsheng
 */
public abstract class AbstractLocalAsyncWorkerProvider<INPUT extends Data, OUTPUT extends Data, WORKER_TYPE extends AbstractLocalAsyncWorker<INPUT, OUTPUT> & QueueExecutor<INPUT>> extends AbstractWorkerProvider<INPUT, OUTPUT, WORKER_TYPE> {

    public abstract int queueSize();

    private final DAOService daoService;
    private final QueueCreatorService<INPUT> queueCreatorService;

    public AbstractLocalAsyncWorkerProvider(DAOService daoService, QueueCreatorService<INPUT> queueCreatorService) {
        this.daoService = daoService;
        this.queueCreatorService = queueCreatorService;
    }

    @Override
    final public WorkerRef create(WorkerCreateListener workerCreateListener) throws ProviderNotFoundException {
        WORKER_TYPE localAsyncWorker = workerInstance(daoService);
        workerCreateListener.addWorker(localAsyncWorker);
        QueueEventHandler<INPUT> queueEventHandler = queueCreatorService.create(queueSize(), localAsyncWorker);
        return new LocalAsyncWorkerRef<>(localAsyncWorker, queueEventHandler);
    }
}