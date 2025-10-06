import { defineStore } from 'pinia';
import { ref } from 'vue';

interface MapFocus {
  lat: number;
  lng: number;
  zoom?: number;
}

export const useUiStore = defineStore('ui', () => {
  const mapFocus = ref<MapFocus | null>(null);
  const reportIncidentModal = ref<{ isOpen: boolean; lat?: number; lng?: number }>({
    isOpen: false,
  });

  function setMapFocus(focus: MapFocus) {
    mapFocus.value = focus;
  }

  function clearMapFocus() {
    mapFocus.value = null;
  }

  function openReportIncidentModal(coords?: { lat: number; lng: number }) {
    reportIncidentModal.value = {
      isOpen: true,
      lat: coords?.lat,
      lng: coords?.lng,
    };
  }

  function closeReportIncidentModal() {
    reportIncidentModal.value.isOpen = false;
  }

  return {
    mapFocus,
    reportIncidentModal,
    setMapFocus,
    clearMapFocus,
    openReportIncidentModal,
    closeReportIncidentModal,
  };
});