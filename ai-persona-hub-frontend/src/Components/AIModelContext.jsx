import React, { createContext, useContext, useEffect, useState } from "react";
import { fetchAvailableModels } from "./FetchAvailableModels.jsx";

const AIModelContext = createContext(null);

export function AIModelProvider({ children }) {
    const [availableModels, setAvailableModels] = useState({});
    const [aiProvider, setAiProvider] = useState("");
    const [modelName, setModelName] = useState("");

    useEffect(() => {
        fetchAvailableModels()
            .then((models) => {
                setAvailableModels(models);

                const providers = Object.keys(models);

                if (providers.length > 0) {
                    const defaultProvider = providers[0];

                    setAiProvider(defaultProvider);
                    setModelName(models[defaultProvider][0]);
                }
            })
            .catch(console.error);
    }, []);

    return (
        <AIModelContext.Provider
            value={{
                availableModels,
                aiProvider,
                modelName,
                setAiProvider,
                setModelName,
            }}
        >
            {children}
        </AIModelContext.Provider>
    );
}

export function useAIModel() {
    return useContext(AIModelContext);
}