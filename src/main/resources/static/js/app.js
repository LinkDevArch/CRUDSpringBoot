/**
 * Aplicación de Gestión de Productos
 * Funcionalidades interactivas para mejorar la experiencia de usuario
 */

document.addEventListener('DOMContentLoaded', function() {
    // Inicialización de tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    });

    // Inicialización de popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl)
    });

    // Animación de entrada para elementos
    animateElements();

    // Inicializar búsqueda en tabla
    initializeTableSearch();

    // Mejorar la visualización de categorías
    setupCategoryColors();
});

/**
 * Aplica animaciones de entrada a los elementos con la clase .fade-in
 */
function animateElements() {
    const elements = document.querySelectorAll('.fade-in');
    elements.forEach((element, index) => {
        element.style.animationDelay = `${index * 0.1}s`;
    });
}

/**
 * Inicializa la funcionalidad de búsqueda en la tabla de productos
 * Ahora solo funciona como complemento a la búsqueda del servidor
 */
function initializeTableSearch() {
    const searchInput = document.getElementById('productSearch');
    if (!searchInput) return;
    
    // Enfoque en el campo de búsqueda al cargar la página
    searchInput.focus();
    
    // Evento para enviar el formulario al presionar Enter
    searchInput.addEventListener('keydown', function(event) {
        if (event.key === 'Enter') {
            event.preventDefault();
            this.closest('form').submit();
        }
    });
    
    // Mostrar mensaje si no hay resultados
    const tableBody = document.querySelector('.app-table tbody');
    if (tableBody && tableBody.querySelectorAll('tr').length === 0) {
        const searchTerm = searchInput.value;
        if (searchTerm && searchTerm.trim() !== '') {
            showNotification(`No se encontraron productos que coincidan con "${searchTerm}"`, 'info');
        }
    }
}

/**
 * Actualiza el contador de productos mostrados
 */
function updateProductCount() {
    const visibleRows = document.querySelectorAll('.app-table tbody tr:not([style*="display: none"])');
    const countElement = document.getElementById('productCount');
    if (countElement) {
        countElement.textContent = visibleRows.length;
    }
}

/**
 * Asigna colores a las categorías para diferenciación visual
 */
function setupCategoryColors() {
    const categoryElements = document.querySelectorAll('.badge-category');
    const categories = new Set();
    
    // Recopilar categorías únicas
    categoryElements.forEach(el => {
        categories.add(el.textContent.trim());
    });
    
    // Colores predefinidos para categorías comunes
    const categoryColors = {
        'Electrónica': '#4361ee',
        'Electrodomésticos': '#3a0ca3',
        'Móviles': '#7209b7',
        'Gaming': '#f72585',
        'Audio': '#4cc9f0',
        'Deportes': '#4895ef',
        'Fotografía': '#560bad',
        'Periféricos': '#f77f00',
        'Calzado': '#e63946',
        'Accesorios': '#fb8500'
    };
    
    // Asignar colores a categorías
    categoryElements.forEach(el => {
        const category = el.textContent.trim();
        if (categoryColors[category]) {
            el.style.backgroundColor = categoryColors[category];
        }
    });
}

/**
 * Confirma la eliminación de un producto con una animación
 * @param {Event} event - Evento del clic
 * @param {number} productId - ID del producto
 * @param {string} productName - Nombre del producto
 */
function confirmDelete(event, productId, productName) {
    event.preventDefault();
    
    const confirmDialog = document.getElementById('confirmDeleteModal');
    const modal = new bootstrap.Modal(confirmDialog);
    
    // Actualizar contenido del modal
    document.getElementById('deleteProductName').textContent = productName;
    document.getElementById('confirmDeleteBtn').setAttribute('data-product-id', productId);
    
    // Mostrar modal
    modal.show();
    
    // Configurar acción de confirmación
    document.getElementById('confirmDeleteBtn').onclick = function() {
        const form = document.querySelector(`form[data-product-id="${productId}"]`);
        if (form) {
            form.submit();
        }
        modal.hide();
    };
}

/**
 * Muestra el banner de notificación con mensaje personalizado
 * @param {string} message - Mensaje a mostrar
 * @param {string} type - Tipo de notificación (success, error, warning, info)
 */
function showNotification(message, type = 'success') {
    const notification = document.getElementById('notification');
    if (!notification) return;
    
    notification.textContent = message;
    notification.className = 'notification';
    notification.classList.add(`notification-${type}`);
    notification.classList.add('show');
    
    setTimeout(() => {
        notification.classList.remove('show');
    }, 5000);
}
