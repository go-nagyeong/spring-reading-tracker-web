import {
    ClassicEditor,
    AccessibilityHelp,
    Alignment,
    AutoLink,
    Autosave,
    BlockQuote,
    Bold,
    Essentials,
    Heading,
    HorizontalLine,
    Indent,
    IndentBlock,
    Italic,
    Link,
    Paragraph,
    Strikethrough,
    Underline,
    Undo
} from 'ckeditor5';
import translations from 'ckeditor5/translations/ko.js';

const ckEditorConfig = {
    toolbar: {
        items: [
            'undo',
            'redo',
            '|',
            'heading',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            '|',
            'horizontalLine',
            'link',
            'blockQuote',
            '|',
            'alignment',
            '|',
            'indent',
            'outdent',
            '|',
            'accessibilityHelp'
        ],
        shouldNotGroupWhenFull: false
    },
    plugins: [
        AccessibilityHelp,
        Alignment,
        AutoLink,
        Autosave,
        BlockQuote,
        Bold,
        Essentials,
        Heading,
        HorizontalLine,
        Indent,
        IndentBlock,
        Italic,
        Link,
        Paragraph,
        Strikethrough,
        Underline,
        Undo
    ],
    heading: {
        options: [
            {
                model: 'paragraph',
                title: 'Paragraph',
                class: 'ck-heading_paragraph'
            },
            {
                model: 'heading1',
                view: 'h1',
                title: 'Heading 1',
                class: 'ck-heading_heading1'
            },
            {
                model: 'heading2',
                view: 'h2',
                title: 'Heading 2',
                class: 'ck-heading_heading2'
            },
            {
                model: 'heading3',
                view: 'h3',
                title: 'Heading 3',
                class: 'ck-heading_heading3'
            },
            {
                model: 'heading4',
                view: 'h4',
                title: 'Heading 4',
                class: 'ck-heading_heading4'
            },
            {
                model: 'heading5',
                view: 'h5',
                title: 'Heading 5',
                class: 'ck-heading_heading5'
            },
            {
                model: 'heading6',
                view: 'h6',
                title: 'Heading 6',
                class: 'ck-heading_heading6'
            }
        ]
    },
    initialData: '',
    link: {
        addTargetToExternalLinks: true,
        defaultProtocol: 'https://',
        decorators: {
            toggleDownloadable: {
                mode: 'manual',
                label: 'Downloadable',
                attributes: {
                    download: 'file'
                }
            }
        }
    },
    placeholder: '',
    language: 'ko',
    translations: [translations]
};


export class CKEditor {
    static instance = null;
    static promise = null;

    constructor(idProperty) {
        if (CKEditor.instance) {
            CKEditor.instance.destroy();
        }
        CKEditor.promise = ClassicEditor.create(document.getElementById(idProperty), ckEditorConfig)
            .then(editor => {
                CKEditor.instance = editor;
            });

        this.initialContent = null;
    }

    async initialize(content) {
        await CKEditor.promise;

        this.initialContent = content;
        this.setContent(content);
    }

    async reset() {
        await CKEditor.promise;

        await this.clear();
        if (this.initialContent) {
            await this.setContent(this.initialContent);
        }
    }

    async clear() {
        await CKEditor.promise;
        this.setContent('');
    }

    async setContent(content) {
        await CKEditor.promise;
        CKEditor.instance.setData(content);
    }

    async getContent() {
        await CKEditor.promise;
        return CKEditor.instance.getData();
    }
}

export class SimpleEditor {
    static instance = null;

    constructor(idProperty) {
        if (SimpleEditor.instance) {
            SimpleEditor.instance.destroy();
        }
        SimpleEditor.instance = new EditorJS(idProperty);

        this.initialContent = null;
    }

    async initialize(content) {
        await SimpleEditor.instance.isReady;

        this.initialContent = content;
        this.setContent(content);
    }

    async reset() {
        await SimpleEditor.instance.isReady;

        await this.clear();
        if (this.initialContent) {
            this.setContent(this.initialContent);
        }
    }

    async clear() {
        await SimpleEditor.instance.isReady;
        SimpleEditor.instance.blocks.clear();
    }

    async setContent(content) {
        await SimpleEditor.instance.isReady;

        const dataBlocks = this.convertHtmlToData(content);
        SimpleEditor.instance.blocks.render({blocks: dataBlocks});
    }

    async getContent() {
        await SimpleEditor.instance.isReady;

        const outputData = await SimpleEditor.instance.save();
        return this.convertDataToHtml(outputData.blocks);
    }

    convertHtmlToData(htmlString) {
        return Array.from(
            new DOMParser().parseFromString(htmlString, 'text/html').querySelectorAll('p')
        ).map(p => ({
            type: "paragraph",
            data: { text: p.textContent }
        }))
    }

    convertDataToHtml(blocks) {
        let convertedHtml = '';
        blocks.map(block => {
            const text = block.data.text;
            const isBlank = text.replaceAll('&nbsp;', '').trim() === '';
            if (!isBlank) {
                switch (block.type) {
                    case 'paragraph':
                        convertedHtml += `<p>${text}</p>`;
                        break;
                    default:
                        console.log('Unknown block type', block.type);
                        break;
                }
            }
        });
        return convertedHtml;
    }
}