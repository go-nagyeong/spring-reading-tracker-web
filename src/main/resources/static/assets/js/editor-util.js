class SimpleEditor {
    static instance = null;

    constructor(idProperty) {
        if (SimpleEditor.instance) {
            SimpleEditor.instance.destroy();
            SimpleEditor.instance = null;
        }
        SimpleEditor.instance = new EditorJS(idProperty);
    }

    async fill(content) {
        await SimpleEditor.instance.isReady;
        this.convertHtmlToData(content).map(data => {
            SimpleEditor.instance.blocks.insert('paragraph', { text: data });
        })
    }

    async save() {
        try {
            const outputData = await SimpleEditor.instance.save();
            return this.convertDataToHtml(outputData.blocks)
        } catch (error) {
            console.error('Error getting content:', error);
        }
    }

    convertHtmlToData(htmlString) {
        const lastIndex = htmlString.lastIndexOf('</p>');
        return htmlString.substring(0, lastIndex).replaceAll('<p>','').split('</p>');
    }

    convertDataToHtml(blocks) {
        let convertedHtml = '';
        blocks.map(block => {
            switch (block.type) {
                case 'paragraph':
                    convertedHtml += `<p>${block.data.text}</p>`;
                    break;
                default:
                    console.log('Unknown block type', block.type);
                    break;
            }
        });
        return convertedHtml;
    }
}