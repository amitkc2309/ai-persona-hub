export async function fetchAvailableModels() {
    const response = await fetch(`/api/config/ai-models`);
    if (!response.ok) {
        throw new Error('Failed to load available AI models');
    }
    return response.json();
}